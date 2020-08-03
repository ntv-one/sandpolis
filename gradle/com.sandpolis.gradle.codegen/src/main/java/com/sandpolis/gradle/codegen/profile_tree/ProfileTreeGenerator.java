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
package com.sandpolis.gradle.codegen.profile_tree;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;

import java.util.List;

import javax.lang.model.SourceVersion;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import com.sandpolis.gradle.codegen.ConfigExtension;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

/**
 * Generator for document bindings.
 *
 * @author cilki
 */
public abstract class ProfileTreeGenerator extends DefaultTask {

	protected static final ClassName DOCUMENT_TYPE = ClassName.bestGuess("com.sandpolis.core.instance.data.Document");

	protected static final String PLUGIN_OID = "1.1";

	protected List<DocumentSpec> flatTree;

	@TaskAction
	public void action() throws Exception {

		// Load the schema
		flatTree = new ObjectMapper().readValue(
				((ConfigExtension) getProject().getExtensions().getByName("codegen")).profileTreeSpec,
				new TypeReference<List<DocumentSpec>>() {
				});

		// Check tree preconditions
		flatTree.forEach(this::validateDocument);

		// Create the root class
		TypeSpec.Builder root = TypeSpec.classBuilder("ProfileTree").addModifiers(PUBLIC, FINAL);

		// Find root document
		flatTree.stream().filter(document -> document.name.equals("Profile")).findAny().ifPresent(document -> {
			processDocument(root, document, "1");
		});
		flatTree.stream().filter(document -> document.name.equals("Plugin")).findAny().ifPresent(document -> {
			processDocument(root, document,
					// Calculate plugin tag
					PLUGIN_OID + "." + Hashing.murmur3_32().hashBytes(getProject().getName().getBytes()).asInt());
		});

		JavaFile.builder(getProject().getName(), root.build())
				.addFileComment("This source file was automatically generated by the Sandpolis codegen plugin.")
				.skipJavaLangImports(true).build().writeTo(getProject().file("gen/main/java"));
	}

	/**
	 * Assert the validity the given document.
	 */
	public void validateDocument(DocumentSpec document) {

		// Name must be a valid Java identifier
		for (var component : document.name.split("\\.")) {
			if (!SourceVersion.isIdentifier(component))
				throw new RuntimeException("Invalid document name: " + document.name);
		}

		// Validate sub-attributes
		if (document.attributes != null) {
			for (var entry : document.attributes.entrySet()) {
				if (entry.getKey() <= 0)
					throw new RuntimeException("Found invalid tag on attribute: " + entry.getValue());

				validateAttribute(entry.getValue());
			}
		}

		// Validate sub-documents
		if (document.documents != null) {
			for (var entry : document.documents.entrySet()) {
				if (entry.getKey() <= 0)
					throw new RuntimeException("Found invalid tag on document: " + entry.getValue());

				// Ensure sub-document exists
				if (flatTree.stream().filter(d -> entry.getValue().equals(d.name)).findAny().isEmpty())
					throw new RuntimeException("Failed to find document: " + entry.getValue());
			}
		}
	}

	/**
	 * Assert the validity the given attribute.
	 */
	public void validateAttribute(AttributeSpec attribute) {

		// Name must be a valid Java identifier
		if (!SourceVersion.isIdentifier(attribute.name))
			throw new RuntimeException("Invalid attribute name: " + attribute.name);

		// Type must be present
		if (attribute.type == null || attribute.type.isEmpty())
			throw new RuntimeException("Missing type on attribute: " + attribute.name);
	}

	/**
	 * Emit the given attribute into the given parent type.
	 */
	public abstract void processAttribute(TypeSpec.Builder parent, AttributeSpec attribute, String oid);

	/**
	 * Emit the given collection.
	 */
	public abstract void processCollection(TypeSpec.Builder parent, DocumentSpec document, String oid);

	/**
	 * Emit the given document.
	 */
	public abstract void processDocument(TypeSpec.Builder parent, DocumentSpec document, String oid);
}
