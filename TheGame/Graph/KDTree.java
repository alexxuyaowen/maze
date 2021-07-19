package TheGame.Graph;

import java.util.List;

public class KDTree implements PointSet {
    Node root; // for the purpose of tests, not making private

    class Node {
        Point item;
        Node left, right;
        int depth;

        Node(Point i, int d) {
            this.item = i;
            this.depth = d;
        }
    }

    public KDTree(List<Point> points) {
        for (Point p : points) {
            insert(p);
        }
    }

    private void insert(Point p) {
        root = insert(1, root, p);
    }

    private Node insert(int depth, Node node, Point p) {
        if (node == null || node.item.equals(p)) { // base case & avoid  duplicates
            return new Node(p, depth);
        }

        if (depth % 2 != 0) {
            if (p.getX() < node.item.getX()) {
                node.left = insert(++depth, node.left, p);
            } else {
                node.right = insert(++depth, node.right, p);
            }
        } else {
            if (p.getY() < node.item.getY()) {
                node.left = insert(++depth, node.left, p);
            } else {
                node.right = insert(++depth, node.right, p);
            }
        }

        return node;
    }

    /* static void printTree(Node n) {
        if (n == null){
            return;
        }

        printTree(n.left);
        System.out.println(n.item);
        printTree(n.right);
    } */

    @Override
    public Point nearest(double x, double y) {
        return nearest(root, new Point(x, y), root).item;
    }

    private Node nearest(Node n, Point p, Node best) {
        if (n == null) {
            return best;
        }

        if (better(n.item, best.item, p)) {
            best = nearest(n, p, n);
        }

        // Node goodSide, badSide;

        double nX = n.item.getX();
        double nY = n.item.getY();
        double pX = p.getX();
        double pY = p.getY();

        if (n.depth % 2 != 0) {
            if (pX < nX) {
                best = nearest(n.left, p, best);
                // if it's on n's left, search left
            } else {
                best = nearest(n.right, p, best);
                // otherwise, search right
            }
        } else {
            if (pY < nY) {
                best = nearest(n.left, p, best);
            } else {
                best = nearest(n.right, p, best);
            }
        }

        // Node best2 = best;
        double shortest = Point.distance(best.item, p);

        if (n.depth % 2 != 0 && Math.pow(nX - pX, 2) < shortest) {
            if (pX < nX) {
                // if it's on the item's left but there may be
                // a nearer point on the right, search right
                best = nearest(n.right, p, best);
            } else {
                best = nearest(n.left, p, best);
            }
        } else {
            if (n.depth % 2 == 0 && Math.pow(nY - pY, 2) < shortest) {
                if (pY < nY) {
                    // if it's on the downside but there
                    // may be a nearer point on the upside, search upside, i.e. rightside
                    best = nearest(n.right, p, best);
                } else {
                    best = nearest(n.left, p, best);
                }
            }
        }

        return best;
        /* if (better(best2.item, best.item, p)) {
            return best2;
        } */

        /* if (depth % 2 != 0) {
            if (Math.pow(n.item.getX() - p.getX(), 2) < shortest) {
                best = nearest(badSide, p, best, ++depth);
            } else {
                badSide = null;
            }
        } else {
            if (Math.pow(n.item.getY() - p.getY(), 2) < shortest) {
                best = nearest(badSide, p, best, ++depth);
            } else {
                badSide = null;
            }
        } */
    }

    // check if p1 is closer to goal than p2 does
    private boolean better(Point p1, Point p2, Point goal) {
        return Point.distance(p1, goal) < Point.distance(p2, goal);
    }

    static void printTree2(Node n, String side) {
        // preOrder print
        if (n != null) {
            String space = " ";
            for (int i = 0; i < n.depth; i++) {
                space += " ";
            }
            System.out.println(space + side + "(" + n.item.getX() + ", " + n.item.getY() + ")");
            printTree2(n.left, "left: ");
            printTree2(n.right, "right: ");
        }
    }
}
