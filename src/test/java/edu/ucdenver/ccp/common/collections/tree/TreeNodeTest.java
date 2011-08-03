package edu.ucdenver.ccp.common.collections.tree;

import static org.junit.Assert.*;
import org.junit.Test;
import edu.ucdenver.ccp.common.test.DefaultTestCase;

public class TreeNodeTest extends DefaultTestCase {

	/**
	 * Testcase1: To check whether the parent node is null or not. The below
	 * test cases are valid test cases.
	 */

	@Test
	public void TreeNode_PositiveInput() {

		TreeNode<Integer> node1 = new TreeNode<Integer>(null, 1);
		TreeNode<Integer> node2 = new TreeNode<Integer>(node1, 2);
		TreeNode<Integer> node3 = new TreeNode<Integer>(node2, 3);
		assertEquals("The parent node should not be null=", node2,
				node3.getParent());

	}

	/**
	 * Testcase2:To check whether the parent node is null or not. The below test
	 * case is negative test case. The node does not have parent. This test case
	 * will pass because even if the parent is null, the program is such a way
	 * that it will accept it
	 */

	@Test
	public void TreeNode_NodeDoesNothaveParent() {

		TreeNode<Integer> node1 = new TreeNode<Integer>(null, 1);
		assertNull("The parent node should not be null=", node1.getParent());
	}

	/**
	 * Testcase3:To check whether the parent node is null or not. The below test
	 * case is negative test case. Here the parent node is given as null.This
	 * test case will pass because even if the parent is null, the program will
	 * accept it
	 */

	@Test
	public void TreeNode_ParentNodeNullValue() {

		TreeNode<Integer> node2 = new TreeNode<Integer>(null, null);
		TreeNode<Integer> node3 = new TreeNode<Integer>(node2, 3);
		assertEquals("The parent node should not be null=", node2,
				node3.getParent());

	}

	/**
	 * Testcase4: To check whether the child node has been added properly to the
	 * parent node. Below is the positive test case.
	 */

	@Test
	public void addChildNode_PositiveTestcase() {

		TreeNode<Integer> node1 = new TreeNode<Integer>(null, 1);
		TreeNode<Integer> node4 = new TreeNode<Integer>(node1, 4);
		TreeNode<Integer> node10 = new TreeNode<Integer>(node4, 10);
		assertEquals(
				"The Child node should be added properly to the parent node=",
				node4, node10.getParent());
	}

	@Test(expected = java.lang.AssertionError.class)
	/**
	 * Testcase5: To check whether the child node has been added properly to the
	 * parent node. Below is the negative test case.
	 */
	public void addChildNode_NegativeTestcase() {

		TreeNode<Integer> node1 = new TreeNode<Integer>(null, 1);
		TreeNode<Integer> node4 = new TreeNode<Integer>(node1, 4);
		new TreeNode<Integer>(node4, 10);
		assertEquals(
				"The Child node should be added properly to the parent node=",
				node4, node4.getParent());
	}

	/**
	 * Testcase6: Returns true if the node has children or it will throw an
	 * error.
	 */

	@Test
	public void hasChildren_WithChildNode() {

		TreeNode<Integer> node1 = new TreeNode<Integer>(null, 1);
		TreeNode<Integer> node2 = new TreeNode<Integer>(node1, 2);
		new TreeNode<Integer>(node2, 3);
		assertTrue(node2.hasChildren());

	}

	/**
	 * Testcase7: Returns false if the node does not have children or it will
	 * throw an error.
	 */

	@Test
	public void hasChildren_WithoutChildNode() {

		TreeNode<Integer> node1 = new TreeNode<Integer>(null, 1);
		TreeNode<Integer> node2 = new TreeNode<Integer>(node1, 2);
		assertFalse(node2.hasChildren());

	}

}
