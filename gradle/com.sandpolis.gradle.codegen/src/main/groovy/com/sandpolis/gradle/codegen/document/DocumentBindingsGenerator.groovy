//============================================================================//
//                                                                            //
//                Copyright © 2015 - 2020 Subterranean Security               //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPL    //
//  as published by the Mozilla Foundation at:                                //
//                                                                            //
//    https://mozilla.org/MPL/2.0                                             //
//                                                                            //
//=========================================================S A N D P O L I S==//
package com.sandpolis.gradle.codegen.document

import static javax.lang.model.element.Modifier.*

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec

import groovy.json.JsonSlurper

import javax.lang.model.SourceVersion

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Generator for document bindings.
 *
 * @author cilki
 */
abstract class DocumentBindingsGenerator extends DefaultTask {

	final DOCUMENT_TYPE = ClassName.bestGuess("com.sandpolis.core.instance.data.Document")

	@TaskAction
	void action () {

		// Load the schema
		def tree = new JsonSlurper().parse(project.file("attribute.json"), 'UTF-8')

		// Check tree preconditions
		validateCollection(tree)

		// Create the root class
		def root = TypeSpec.classBuilder("DocumentBindings")
			.addModifiers(PUBLIC, FINAL)

		// Generate the bindings
		processCollection(root, tree)

		JavaFile.builder(project.name, root.build())
			.addFileComment("This source file was automatically generated by the Sandpolis codegen plugin.")
			.skipJavaLangImports(true).build().writeTo(project.file("gen/main/java"));
	}

	/**
	 * Assert the validity the given collection.
	 */
	void validateCollection(collection) {
		validateDocument(collection)
	}

	/**
	 * Assert the validity the given document.
	 */
	void validateDocument(document) {

		// Name must be a valid Java identifier
		if (!SourceVersion.isIdentifier(document.name))
			throw new RuntimeException("Invalid document name: ${document.name}")

		// Tag must be positive
		if (document.tag == null || document.tag <= 0)
			throw new RuntimeException("Found invalid tag (${document.tag}) on document: ${document.name}")

		// Validate subattributes
		if (document.attributes != null) {
			for (def subattribute : document.attributes) {
				validateAttribute(subattribute)
			}
		}

		// Validate subdocuments
		if (document.documents != null) {
			for (def subdocument : document.documents) {
				validateDocument(subdocument)
			}
		}
	}

	/**
	 * Assert the validity the given attribute.
	 */
	void validateAttribute(attribute) {

		// Name must be a valid Java identifier
		if (!SourceVersion.isIdentifier(attribute.name))
			throw new RuntimeException("Invalid attribute name: ${attribute.name}")

		// Tag must be positive
		if (attribute.tag == null || attribute.tag <= 0)
			throw new RuntimeException("Found invalid tag (${attribute.tag}) on attribute: ${attribute.name}")

		// Type must be present
		if (attribute.type == null)
			throw new RuntimeException("Missing type on attribute: ${attribute.name}")

		// Subattributes are not allowed
		if (attribute.attributes != null)
			throw new RuntimeException("Invalid 'attributes' field on attribute: ${attribute.name}")
	}

	/**
	 * Emit the given attribute into the given parent type.
	 */
	abstract void processAttribute(parent, attribute)

	/**
	 * Emit the given collection.
	 */
	abstract void processCollection(parent, collection)

	/**
	 * Emit the given document.
	 */
	abstract void processDocument(parent, document)

	/**
	 * Convert snake-case to camel-case.
	 */
	String camel(text) {
		return text.replaceAll("(_)([A-Za-z0-9])", { Object[] it -> it[2].toUpperCase() })
	}
}
