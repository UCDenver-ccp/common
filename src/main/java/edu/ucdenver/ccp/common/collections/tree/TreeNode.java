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
import java.util.Iterator;
import java.util.List;

public class TreeNode<E> {

	protected E nodeValue;

	protected TreeNode<E> parent;

	protected List<TreeNode<E>> children;

	protected int depth = -1;

	/**
	 * Constructor for tree node. Automatically links this node to the input parent. Parent = null for a root node.
	 * 
	 * @param parent
	 * @param nodeValue
	 */
	public TreeNode(TreeNode<E> parent, E nodeValue) {
		this.nodeValue = nodeValue;
		this.parent = parent;
		this.children = new ArrayList<TreeNode<E>>();
		if (parent != null) {
			parent.addChildNode(this);
		}
	}

	/**
	 * Returns the parent of this tree node
	 * 
	 * @return
	 */
	public TreeNode<E> getParent() {
		return parent;
	}

	/**
	 * Set the parent of this node to the input node
	 * 
	 * @param parent
	 */
	public void setParent(TreeNode<E> parent) {
		this.parent = parent;
	}

	/**
	 * Adds a child node to this node
	 * 
	 * @param child
	 */
	public void addChildNode(TreeNode<E> child) {
		child.setParent(this);
		children.add(child);
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	/**
	 * Returns an iterator over the children of this node
	 * 
	 * @return
	 */
	public Iterator<TreeNode<E>> getChildren() {
		return children.iterator();
	}

	/**
	 * Returns true if this node has children, false if it is a leaf node.
	 * 
	 * @return
	 */
	public boolean hasChildren() {
		return children.size() > 0;
	}

	/**
	 * Returns the value stored by this node
	 * 
	 * @return
	 */
	public E getNodeValue() {
		return nodeValue;
	}
}
