package edu.ucdenver.ccp.common.lucene;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.lucene.DocumentFeeder;
import edu.ucdenver.ccp.common.lucene.LuceneUtil;

public class LuceneUtilTest {

	static {
		BasicConfigurator.configure();
	}

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private File sampleEmployeeIndexDirectory;
	private File sampleEmployeeRAMIndexDirectory;

	@Before
	public void setUp() throws Exception {
		sampleEmployeeIndexDirectory = folder.newFolder("sampleEmployeeIndex");
		sampleEmployeeRAMIndexDirectory = folder.newFolder("sampleEmployeeIndex_RAM");
		createSampleEmployeeIndex();
		createSampleEmployeeIndex_RAM();
	}

	private void createSampleEmployeeIndex() throws Exception {
		LuceneUtil luceneUtil = new LuceneUtil(sampleEmployeeIndexDirectory);
		luceneUtil.populateNewIndex(new EmployeeDocumentFeeder(getSampleEmployeeList()),
				LuceneUtil.DirectoryType.FILESYSTEM, true);
	}

	private void createSampleEmployeeIndex_RAM() throws Exception {
		LuceneUtil luceneUtil = new LuceneUtil(sampleEmployeeRAMIndexDirectory);
		luceneUtil.addToIndex(LuceneUtil.DirectoryType.RAM, new EmployeeDocumentFeeder(getSampleEmployeeList()),
				LuceneUtil.Overwrite.YES, true);
	}

	private List<Employee> getSampleEmployeeList() {
		return CollectionsUtil.createList(new Employee(0, "Bob", "Sanitation"),
				new Employee(1, "Judith", "Management"), new Employee(2, "Sam", "Litigation"), new Employee(3, "Paul",
						"Management"), new Employee(4, "Dirk", "Maintenance"));
	}

	private List<Employee> getMoreSampleEmployeeList() {
		return CollectionsUtil.createList(new Employee(5, "John", "Lunch"), new Employee(6, "Peter", "WaterCooler"),
				new Employee(7, "Bert", "Litigation"));
	}

	@Test
	public void testGetIndexedDocumentCount() throws Exception {
		LuceneUtil luceneUtil = new LuceneUtil(sampleEmployeeIndexDirectory);
		assertEquals(5, luceneUtil.getIndexedDocumentCount());
	}

	@Test
	public void testCreateSampleEmployeeIndex() throws Exception {
		validateEmployeeIndex(sampleEmployeeIndexDirectory, getSampleEmployeeList());
	}

	@Test
	public void testCreateSampleEmployeeIndex_usingLuceneRamDirectory() throws Exception {
		validateEmployeeIndex(sampleEmployeeRAMIndexDirectory, getSampleEmployeeList());
	}

	private void validateEmployeeIndex(File indexDirectory, List<Employee> employeeList) throws Exception {
		IndexReader indexReader = IndexReader.open(FSDirectory.open(indexDirectory));
		assertEquals(String.format("Should have the same number of documents as sample employees."), employeeList
				.size(), indexReader.numDocs());
		for (int i = 0; i < indexReader.numDocs(); i++) {
			checkEmployeeFields(employeeList.get(i), indexReader.document(i));
		}
		indexReader.close();
	}

	private void checkEmployeeFields(Employee employee, Document document) {
		assertEquals(String.format("IDs should be equal."), employee.getUniqueID(), Integer.parseInt(document.getField(
				EmployeeDocumentBuilder.ID_FIELD).stringValue()));
		assertEquals(String.format("Names should be equal."), employee.getName(), document.getField(
				EmployeeDocumentBuilder.NAME_FIELD).stringValue());
		assertEquals(String.format("Departments should be equal."), employee.getDepartment(), document.getField(
				EmployeeDocumentBuilder.DEPARTMENT_FIELD).stringValue());
	}

	@Test
	public void testDeleteBasedOnIDs() throws Exception {
		LuceneUtil luceneUtil = new LuceneUtil(sampleEmployeeIndexDirectory);
		assertEquals(5, luceneUtil.getIndexedDocumentCount());
		luceneUtil.deleteFromIndex(EmployeeDocumentBuilder.ID_FIELD, CollectionsUtil.createList("3", "4"));
		ArrayList<Employee> expectedListWithDeletions = new ArrayList<Employee>(getSampleEmployeeList());
		expectedListWithDeletions.remove(4);
		expectedListWithDeletions.remove(3);
		validateEmployeeIndex(sampleEmployeeIndexDirectory, expectedListWithDeletions);
	}

	@Test
	public void testPopulateExistingIndex_usingFileSystemDirectory() throws Exception {
		testPopulateExistingIndex(LuceneUtil.DirectoryType.FILESYSTEM);
	}

	@Test
	public void testPopulateExistingIndex_usingRAMDirectory() throws Exception {
		testPopulateExistingIndex(LuceneUtil.DirectoryType.RAM);
	}

	private void testPopulateExistingIndex(LuceneUtil.DirectoryType directoryType) throws Exception {
		LuceneUtil luceneUtil = new LuceneUtil(sampleEmployeeIndexDirectory);
		luceneUtil.populateExistingIndex(new EmployeeDocumentFeeder(getMoreSampleEmployeeList()), directoryType, true);
		assertEquals(String.format("Should have 5+3=8 documents indexed."), 8, luceneUtil.getIndexedDocumentCount());
		ArrayList<Employee> expectedEmployeeList = new ArrayList<Employee>(getSampleEmployeeList());
		expectedEmployeeList.addAll(getMoreSampleEmployeeList());
		validateEmployeeIndex(sampleEmployeeIndexDirectory, expectedEmployeeList);
	}

	private static class EmployeeDocumentFeeder extends DocumentFeeder {

		private final Iterator<Employee> employeeIter;
		private final EmployeeDocumentBuilder builder = new EmployeeDocumentBuilder();

		public EmployeeDocumentFeeder(List<Employee> employeeList) {
			this.employeeIter = employeeList.iterator();
		}

		@Override
		public boolean hasNext() {
			return employeeIter.hasNext();
		}

		@Override
		public Document next() {
			return builder.buildEmployeeDocument(employeeIter.next());
		}

		@Override
		public void close() {
			throw new UnsupportedOperationException("Close() is not supported/needed for this DocumentFeeder class.");

		}

	}

	private static class Employee {
		private final int uniqueID;
		private final String name;
		private final String department;

		public Employee(int uniqueID, String name, String department) {
			super();
			this.uniqueID = uniqueID;
			this.name = name;
			this.department = department;
		}

		public int getUniqueID() {
			return uniqueID;
		}

		public String getName() {
			return name;
		}

		public String getDepartment() {
			return department;
		}

	}

	private static class EmployeeDocumentBuilder {
		static final String ID_FIELD = "id";
		static final String NAME_FIELD = "name";
		static final String DEPARTMENT_FIELD = "department";

		public Document buildEmployeeDocument(Employee employee) {
			Document document = new Document();
			document.add(new Field(ID_FIELD, Integer.toString(employee.getUniqueID()), Field.Store.YES,
					Field.Index.NOT_ANALYZED));
			document.add(new Field(NAME_FIELD, employee.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
			document.add(new Field(DEPARTMENT_FIELD, employee.getDepartment(), Field.Store.YES, Field.Index.ANALYZED));
			return document;
		}
	}
}
