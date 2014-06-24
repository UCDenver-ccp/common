package edu.ucdenver.ccp.common.collections.tree;

/*
 * #%L
 * Colorado Computational Pharmacology's common module
 * %%
 * Copyright (C) 2012 - 2014 Regents of the University of Colorado
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Regents of the University of Colorado nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A generic tree data structure
 * 
 * @author Bill Baumgartner
 * 
 * @param <T>
 */
public class Tree<T> {

	private TreeNode<T> rootNode;
	private Collection<TreeNode<T>> nodes;

	public Tree(TreeNode<T> rootNode) {
		this.rootNode = rootNode;
		this.nodes = new ArrayList<TreeNode<T>>();
		if (rootNode != null) {
			updateNodesFromRoot();
		}
	}

	public Tree(TreeNode<T> rootNode, Collection<TreeNode<T>> nodes) {
		this.rootNode = rootNode;
		this.nodes = nodes;
	}

	/**
	 * This method traverses the tree starting at the root node and adds the nodes it finds to the "nodes" variable
	 */
	private void updateNodesFromRoot() {
		this.nodes = new ArrayList<TreeNode<T>>();
		List<TreeNode<T>> nodeList = doDepthFirstTraversal(rootNode, 0);
		for (TreeNode<T> treeNode : nodeList) {
			nodes.add(treeNode);
		}
	}

	/**
	 * Returns the single root node for this tree structure
	 * 
	 * @return
	 */
	public TreeNode<T> getRootNode() {
		return rootNode;
	}

	public void setRootNode(TreeNode<T> newRootNode) {
		if (rootNode != null) {
			newRootNode.addChildNode(rootNode);
		}
		rootNode = newRootNode;
		updateNodesFromRoot();

	}

	/**
	 * Returns the collection of nodes that comprise this tree structure
	 * 
	 * @return
	 */
	public Collection<TreeNode<T>> getNodes() {
		return nodes;
	}

	/**
	 * Returns an Iterator over the nodes in this tree structure when doing a depth-first search
	 * 
	 * @return
	 */
	public Iterator<TreeNode<T>> depthFirstTraversal() {
		final List<TreeNode<T>> nodeList = doDepthFirstTraversal(rootNode, 0);

		return nodeList.iterator();

		// return new Iterator<T>() {
		// private T nextNode = null;
		// private int index = 0;
		//
		// public boolean hasNext() {
		// if (nextNode == null) {
		// if (index < nodeList.size()) {
		// nextNode = nodeList.get(index++);
		// return true;
		// }
		// return false;
		// }
		// return true;
		// }
		//
		// public T next() {
		// if (!hasNext()) {
		// throw new NoSuchElementException();
		// }
		// T nodeToReturn = nextNode;
		// nextNode = null;
		// return nodeToReturn;
		// }
		//
		// public void remove() {
		// throw new UnsupportedOperationException(
		// "The remove() method is not supported for this iterator.");
		// }
		// };
	}

	/**
	 * Recursive method for conducting a depth-first traversal on this tree structure
	 * 
	 * @param node
	 * @return
	 */
	private List<TreeNode<T>> doDepthFirstTraversal(TreeNode<T> node, int depth) {
		List<TreeNode<T>> nodeList = new ArrayList<TreeNode<T>>();
		node.setDepth(depth);
		nodeList.add(node);
		if (node.hasChildren()) {
			Iterator<TreeNode<T>> childNodeIter = node.getChildren();
			while (childNodeIter.hasNext()) {
				nodeList.addAll(doDepthFirstTraversal(childNodeIter.next(), depth + 1));
			}
		}
		return nodeList;
	}

	/**
	 * Returns true if this tree is empty, i.e. it has no nodes
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return nodes.size() == 0;
	}

	/**
	 * Returns the number of nodes in this tree
	 * 
	 * @return
	 */
	public int size() {
		return nodes.size();
	}

}
