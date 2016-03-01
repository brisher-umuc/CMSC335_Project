import javax.swing.tree.DefaultMutableTreeNode;

/**
 * File: ToolTipTreeNode
 * Date: 2/10/16
 * Author: ben risher
 * Purpose:  provide tooltips for my jtree
 *
 * source code taken (almost) verbatim from http://www.java2s.com/Code/Java/Swing-Components/ToolTipTreeExample.htm
 */
class ToolTipTreeNode extends DefaultMutableTreeNode {
    private String toolTipText = "";
    public static final long serialVersionUID = 446273; // ND:

    public ToolTipTreeNode(String str, String toolTipText) {
        super(str);
        if (toolTipText.equals("")) {
            return;
        }
        this.toolTipText = toolTipText;
    }

    public ToolTipTreeNode(String str) {
        super(str);
    }

    public void setToolTipText(String toolTipText) {
        this.toolTipText = toolTipText;
    }


    public String getToolTipText() {
        return toolTipText;
    }
}

