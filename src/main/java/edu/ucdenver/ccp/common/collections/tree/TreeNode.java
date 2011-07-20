package edu.ucdenver.ccp.common.collections.tree;

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
