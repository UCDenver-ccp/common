/*
 Copyright (c) 2012, Regents of the University of Colorado
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
    list of conditions and the following disclaimer.
   
 * Redistributions in binary form must reproduce the above copyright notice, 
    this list of conditions and the following disclaimer in the documentation 
    and/or other materials provided with the distribution.
   
 * Neither the name of the University of Colorado nor the names of its 
    contributors may be used to endorse or promote products derived from this 
    software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.ucdenver.ccp.common.collections.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import edu.ucdenver.ccp.common.test.DefaultTestCase;
import java.util.ArrayList;
import java.util.Iterator;

@Ignore("trying to see if these test a responsible for the stall that occurs during the Hudson build")
public class TreeTest extends DefaultTestCase {

	/**
	 * Testcase1: If the root node is null, it should throw an exception. Below
	 * is positive test case, and the root node is not null
	 */

	@Test
	public void Tree_CheckingRootNode() {

		TreeNode<Integer> node1 = new TreeNode<Integer>(null, 1);
		new TreeNode<Integer>(node1, 2);
		Tree<Integer> tree = new Tree<Integer>(node1);
		assertEquals("The root for this tree node is=", node1,
				tree.getRootNode());
	}

	/**
	 * Testcase2: If the root node is null, it should throw an exception. Below
	 * is negative test case, and the value of root node is given as null
	 */

	@Test(expected = java.lang.AssertionError.class)
	public void Tree_NullRootNodeValue() {

		TreeNode<Integer> node1 = new TreeNode<Integer>(null, null);
		Tree<Integer> tree = new Tree<Integer>(null);
		assertEquals("The root for this Invalid tree structure is=", node1,
				tree.getRootNode());
	}

	/**
	 * Testcase3: This is a negative test case. To check whether the root node
	 * has parent node. In real case, the root node should not have parent node.
	 * (i.e) parent = null means root node. The below test case is passing, but
	 * in actual case it should fail.
	 */

	@Ignore("Need to modified the code so that it does not allow the root node to have parent")
	@Test(expected = IllegalArgumentException.class)
	public void Tree_CheckRootNodehasParentNode() {

		TreeNode<Integer> node1 = new TreeNode<Integer>(null, 1);
		TreeNode<Integer> node2 = new TreeNode<Integer>(node1, 2);
		TreeNode<Integer> node3 = new TreeNode<Integer>(node2, 3);
		Tree<Integer> tree1 = new Tree<Integer>(node2);
		assertEquals("The root for this Invalid tree structure is=", node3,
				tree1.getRootNode());
	}

	/**
	 * Testcase4: To check whether the new root node is set properly. Below is a
	 * positive test case.
	 */

	@Test
	public void setRootNode_CheckNewRootNode() {

		TreeNode<Integer> node3 = new TreeNode<Integer>(null, 1);
		TreeNode<Integer> node2 = new TreeNode<Integer>(node3, 2);
		TreeNode<Integer> node1 = new TreeNode<Integer>(node2, 3);
		Tree<Integer> tree = new Tree<Integer>(node1);
		tree.setRootNode(node3);
		assertEquals("The New root for this tree node is=", node3,
				tree.getRootNode());
	}

	/**
	 * Testcase5: To check whether the new root node is set properly. Below is a
	 * negative test case. Setting the new root node as null.
	 */

	@Test(expected = NullPointerException.class)
	public void setRootNode_CheckNewRootNodeNullvalue() {

		TreeNode<Integer> node3 = new TreeNode<Integer>(null, 1);
		TreeNode<Integer> node2 = new TreeNode<Integer>(node3, 2);
		TreeNode<Integer> node1 = new TreeNode<Integer>(node2, 3);
		Tree<Integer> tree = new Tree<Integer>(node1);
		tree.setRootNode(null);
		assertEquals("The New root for this tree node is=", node3,
				tree.getRootNode());
	}

	/**
	 * Testcase6: To check whether the new root node is set properly. Below is a
	 * negative test case. Setting the old root node itself as new root node.
	 */

	@Test(expected = java.lang.StackOverflowError.class)
	public void setRootNode_SettingSameRootNode() {

		TreeNode<Integer> node3 = new TreeNode<Integer>(null, 1);
		TreeNode<Integer> node2 = new TreeNode<Integer>(node3, 2);
		TreeNode<Integer> node1 = new TreeNode<Integer>(node2, 3);
		Tree<Integer> tree = new Tree<Integer>(node1);
		tree.setRootNode(node1);
		assertEquals("The New root for this tree node is=", node1,
				tree.getRootNode());

	}

	/**
	 * Testcase7: This is a negative test case. To check whether the new root
	 * node has parent node. In real case, the new root node should not have
	 * parent node. The below test case is passing, but in actual case it should
	 * fail.
	 */

	@Ignore("Need to modified the code so that it does not allow the new root node to have parent")
	@Test
	public void setRootNode_CheckNewRootNodehasParentNode() {

		TreeNode<Integer> node3 = new TreeNode<Integer>(null, 1);
		TreeNode<Integer> node2 = new TreeNode<Integer>(node3, 2);
		TreeNode<Integer> node1 = new TreeNode<Integer>(node2, 3);
		Tree<Integer> tree = new Tree<Integer>(node1);
		tree.setRootNode(node2);
		assertEquals("The New root for this tree node is=", node2,
				tree.getRootNode());
	}

	/**
	 * Testcase8: To check the traversal of tree. Below is positive test case.
	 */

	@Test
	public void depthFirstTraversal_ValidInput() {

		TreeNode<Integer> node1 = new TreeNode<Integer>(null, 1);
		TreeNode<Integer> node2 = new TreeNode<Integer>(node1, 2);
		TreeNode<Integer> node3 = new TreeNode<Integer>(node1, 3);
		TreeNode<Integer> node4 = new TreeNode<Integer>(node1, 4);
		TreeNode<Integer> node5 = new TreeNode<Integer>(node2, 5);
		TreeNode<Integer> node6 = new TreeNode<Integer>(node2, 6);
		TreeNode<Integer> node7 = new TreeNode<Integer>(node3, 7);
		TreeNode<Integer> node8 = new TreeNode<Integer>(node3, 8);
		TreeNode<Integer> node9 = new TreeNode<Integer>(node3, 9);
		TreeNode<Integer> node10 = new TreeNode<Integer>(node4, 10);

		Tree<Integer> tree = new Tree<Integer>(node1);

		List<TreeNode<Integer>> actualNodeList = new ArrayList<TreeNode<Integer>>();
		Iterator<TreeNode<Integer>> dfIter = tree.depthFirstTraversal();

		while (dfIter.hasNext()) {
			TreeNode<Integer> next = dfIter.next();
			actualNodeList.add(next);
		}

		List<TreeNode<Integer>> expectedNodeList = new ArrayList<TreeNode<Integer>>();
		expectedNodeList.add(node1);
		expectedNodeList.add(node2);
		expectedNodeList.add(node5);
		expectedNodeList.add(node6);
		expectedNodeList.add(node3);
		expectedNodeList.add(node7);
		expectedNodeList.add(node8);
		expectedNodeList.add(node9);
		expectedNodeList.add(node4);
		expectedNodeList.add(node10);

		System.out.println("Expected Node list :"
				+ expectedNodeList.iterator().toString());
		assertEquals(expectedNodeList, actualNodeList);
	}

	/**
	 * Testcase9: To check isEmpty() method. Returns true if this tree is empty,
	 * i.e. it has no nodes
	 */

	@Test
	public void isEmpty_ValidInput() {

		Tree<Integer> tree = new Tree<Integer>(null);
		assertTrue(tree.isEmpty());

	}

	/**
	 * Testcase10: To check isEmpty() method. Returns false if this tree has
	 * nodes
	 */

	@Test
	public void isEmpty_ValidInput2() {

		TreeNode<Integer> node1 = new TreeNode<Integer>(null, 1);
		TreeNode<Integer> node2 = new TreeNode<Integer>(node1, 2);
		TreeNode<Integer> node3 = new TreeNode<Integer>(node2, 3);
		new TreeNode<Integer>(node3, 4);

		Tree<Integer> tree = new Tree<Integer>(node1);
		assertFalse(tree.isEmpty());

	}

	/**
	 * Testcase11: To check size() method. Returns size of the method. Below is
	 * the positive test case.
	 */

	@Test
	public void size_PositiveTestcase() {

		TreeNode<Integer> node1 = new TreeNode<Integer>(null, 1);
		TreeNode<Integer> node2 = new TreeNode<Integer>(node1, 2);
		TreeNode<Integer> node3 = new TreeNode<Integer>(node2, 3);
		new TreeNode<Integer>(node3, 4);

		Tree<Integer> tree = new Tree<Integer>(node1);

		System.out.println("The size of tree for Valid Input =" + tree.size());
		assertEquals("The size of tree for Invalid input=", (int) 4,
				tree.size());
	}

	/**
	 * Testcase12: To check size() method. Returns size of the method. Below is
	 * the negative test case.
	 */

	@Test
	public void size_NegativeTestcase() {

		TreeNode<Integer> node3 = new TreeNode<Integer>(null, 1);
		TreeNode<Integer> node2 = new TreeNode<Integer>(node3, 2);
		TreeNode<Integer> node1 = new TreeNode<Integer>(node2, 3);
		new TreeNode<Integer>(node1, 4);

		Tree<Integer> tree = new Tree<Integer>(node1);

		System.out.println("The size of tree for Invalid input=" + tree.size());

		assertEquals("The size of tree for Invalid input=", (int) 2,
				tree.size());

	}

}
