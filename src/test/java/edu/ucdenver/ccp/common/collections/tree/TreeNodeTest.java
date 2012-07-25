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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import edu.ucdenver.ccp.common.test.DefaultTestCase;

@Ignore("trying to see if these test a responsible for the stall that occurs during the Hudson build")
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
