/******************************************************************************
 *                                                                            *
 *                    Copyright 2018 Subterranean Security                    *
 *                                                                            *
 *  Licensed under the Apache License, Version 2.0 (the "License");           *
 *  you may not use this file except in compliance with the License.          *
 *  You may obtain a copy of the License at                                   *
 *                                                                            *
 *      http://www.apache.org/licenses/LICENSE-2.0                            *
 *                                                                            *
 *  Unless required by applicable law or agreed to in writing, software       *
 *  distributed under the License is distributed on an "AS IS" BASIS,         *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 *  See the License for the specific language governing permissions and       *
 *  limitations under the License.                                            *
 *                                                                            *
 *****************************************************************************/
package com.sandpolis.core.storage.ormlite;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import javax.persistence.Column;
import javax.persistence.Id;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.sandpolis.core.instance.storage.StoreProvider;
import com.sandpolis.core.instance.storage.database.Database;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import net.jodah.concurrentunit.Waiter;

// This class should inherit tests from StoreProviderTest
class OrmliteStoreProviderTest {

	static class TestObject {

		@Id
		private long id;

		@Column
		private String name;

		public TestObject() {
		}

		public TestObject(long id, String name) {
			this.id = id;
			this.name = name;
		}

		public long getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return id + ": " + name;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof TestObject) {
				TestObject o = (TestObject) obj;
				return o.id == this.id && o.name.equals(this.name);
			}

			return false;
		}

	}

	private TestObject o1 = new TestObject(1L, "One");
	private TestObject o2 = new TestObject(2L, "Two");
	private TestObject o3 = new TestObject(3L, "Three");
	private TestObject o4 = new TestObject(4L, "Four");
	private TestObject o5 = new TestObject(5L, "Five");
	private TestObject o6 = new TestObject(6L, "Six");

	@ParameterizedTest
	@MethodSource("implementations")
	void testAdd(OrmliteStoreProvider<TestObject> provider) {
		provider.add(o1);
		provider.add(o2);
		provider.add(o3);
		provider.add(o4);
		provider.add(o5);
		provider.add(o6);
	}

	@ParameterizedTest
	@MethodSource("implementations")
	void testGetId(OrmliteStoreProvider<TestObject> provider) {
		provider.add(o1);
		provider.add(o2);
		provider.add(o3);
		provider.add(o4);
		provider.add(o5);
		provider.add(o6);

		assertEquals(o1, provider.get(1L).get());
		assertEquals(o2, provider.get(2L).get());
		assertEquals(o4, provider.get(4L).get());

		// Repeat
		assertEquals(o1, provider.get(1L).get());
		assertEquals(o2, provider.get(2L).get());
		assertEquals(o4, provider.get(4L).get());
	}

	@ParameterizedTest
	@MethodSource("implementations")
	void testGetByField(OrmliteStoreProvider<TestObject> provider) {
		provider.add(o1);
		provider.add(o2);
		provider.add(o3);
		provider.add(o4);
		provider.add(o5);
		provider.add(o6);

		assertEquals(o1, provider.get("name", "One").get());
		assertEquals(o2, provider.get("name", "Two").get());

		// Repeat
		assertEquals(o1, provider.get("name", "One").get());
		assertEquals(o2, provider.get("name", "Two").get());
	}

	@ParameterizedTest
	@MethodSource("implementations")
	void testRemove(OrmliteStoreProvider<TestObject> provider) {
		provider.add(o1);
		provider.add(o2);
		provider.add(o3);
		provider.add(o4);
		provider.add(o5);
		provider.add(o6);

		provider.remove(o2);
		provider.remove(o4);
		provider.remove(o6);

		assertFalse(provider.get(2L).isPresent());
		assertFalse(provider.get(4L).isPresent());
		assertFalse(provider.get(6L).isPresent());

		assertEquals(o1, provider.get(1L).get());
		assertEquals(o3, provider.get(3L).get());
		assertEquals(o5, provider.get(5L).get());
	}

	@ParameterizedTest
	@MethodSource("implementations")
	void testExistsById(OrmliteStoreProvider<TestObject> provider) {
		provider.add(o1);
		provider.add(o2);
		provider.add(o3);
		provider.add(o4);
		provider.add(o5);
		provider.add(o6);

		provider.remove(o2);
		provider.remove(o4);
		provider.remove(o6);

		assertFalse(provider.exists(2L));
		assertFalse(provider.exists(4L));
		assertFalse(provider.exists(6L));

		assertTrue(provider.exists(1L));
		assertTrue(provider.exists(3L));
		assertTrue(provider.exists(5L));
	}

	@ParameterizedTest
	@MethodSource("implementations")
	void testExistsByField(OrmliteStoreProvider<TestObject> provider) {
		provider.add(o1);
		provider.add(o2);
		provider.add(o3);
		provider.add(o4);
		provider.add(o5);
		provider.add(o6);

		provider.remove(o2);
		provider.remove(o4);
		provider.remove(o6);

		assertFalse(provider.exists("name", "Two"));
		assertFalse(provider.exists("name", "Four"));
		assertFalse(provider.exists("name", "Six"));

		assertTrue(provider.exists("name", "One"));
		assertTrue(provider.exists("name", "Three"));
		assertTrue(provider.exists("name", "Five"));
	}

	@ParameterizedTest
	@MethodSource("implementations")
	void testCount(OrmliteStoreProvider<TestObject> provider) {
		assertEquals(0, provider.count());
		provider.add(o1);
		assertEquals(1, provider.count());
		provider.add(o2);
		assertEquals(2, provider.count());
		provider.add(o3);
		assertEquals(3, provider.count());
		provider.add(o4);
		assertEquals(4, provider.count());
		provider.add(o5);
		assertEquals(5, provider.count());
		provider.add(o6);
		assertEquals(6, provider.count());
		provider.remove(o2);
		assertEquals(5, provider.count());
	}

	@ParameterizedTest
	@MethodSource("implementations")
	void testStream(OrmliteStoreProvider<TestObject> provider) {
		provider.add(o1);
		provider.add(o2);
		provider.add(o4);

		try (Stream<TestObject> stream = provider.stream()) {
			assertArrayEquals(new TestObject[] { o1, o2, o4 }, stream.toArray(TestObject[]::new));
		}
	}

	@ParameterizedTest
	@MethodSource("implementations")
	void testConcurrency(StoreProvider<TestObject> provider) throws Exception {
		Set<Thread> threads = new HashSet<>();
		Random rand = new Random();
		Waiter waiter = new Waiter();

		// Add some concurrent mutators
		for (TestObject o : new TestObject[] { o1, o2, o3, o4, o5, o6 })
			threads.add(new Thread(() -> {
				for (int i = 0; i < rand.nextInt(50); i++) {
					try {
						provider.add(o);
						Thread.sleep(rand.nextInt(200));
						provider.remove(o);
					} catch (Throwable e) {
						waiter.fail(e);
					}
				}
				waiter.resume();
			}));

		// Add some get requests
		for (TestObject o : new TestObject[] { o1, o2, o3, o4, o5, o6 })
			threads.add(new Thread(() -> {
				for (int i = 0; i < rand.nextInt(50); i++) {
					try {
						// May be null or nonnull
						provider.get(o.getId());
						Thread.sleep(rand.nextInt(200));
					} catch (Throwable e) {
						waiter.fail(e);
					}
				}
				waiter.resume();
			}));

		// Add some iterators
		for (TestObject o : new TestObject[] { o1, o2, o3, o4, o5, o6 })
			threads.add(new Thread(() -> {
				for (int i = 0; i < rand.nextInt(50); i++) {
					try (Stream<TestObject> stream = provider.stream()) {
						waiter.assertTrue(stream.count() <= 6);
						Thread.sleep(rand.nextInt(200));
					} catch (Throwable e) {
						waiter.fail(e);
					}
				}
				waiter.resume();
			}));

		// Start everything at once
		for (Thread thread : threads)
			thread.start();
		waiter.await(30000, 18);
	}

	/**
	 * A MariaDB database for testing.
	 */
	private static DB db;

	@BeforeAll
	private static void setup() throws ManagedProcessException, InterruptedException {
		db = DB.newEmbeddedDB(8001);
		db.start();

		// TODO find a better way to wait for the database to load
		Thread.sleep(9000);
	}

	@AfterAll
	private static void cleanup() throws ManagedProcessException {
		db.stop();
	}

	static Stream<StoreProvider<TestObject>> implementations()
			throws URISyntaxException, IOException, SQLException, ManagedProcessException {
		Database sqlite = new OrmliteDatabaseFactory().sqlite(new Class[] { TestObject.class },
				new Database("jdbc:sqlite:" + Files.createTempFile(null, null).toUri().toURL()));

		Database mysql = new OrmliteDatabaseFactory().mysql(new Class[] { TestObject.class },
				new Database("jdbc:mysql://127.0.0.1:8001/test?serverTimezone=America/Chicago", "root", ""));

		// Reset table
		((OrmliteConnection) mysql.getConnection()).truncate(TestObject.class);

		return Stream.of(sqlite.getConnection().provider(TestObject.class),
				mysql.getConnection().provider(TestObject.class));
	}

}
