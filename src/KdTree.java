/* ****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 *************************************************************************** */


import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class KdTree {

    private static final boolean VERTICAL = true;
    private static final boolean HORIZONTAL = false;

    private class Node {
        boolean divide;
        Node left, right;
        Point2D point;

        private Node(boolean d, Point2D p) {
            this.divide = d;
            this.point = p;
            this.left = null;
            this.right = null;
        }

    }

    private Node root;
    private int n;

    // construct an empty set of points
    public KdTree() {
        this.root = null;
        this.n = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        return (this.root == null);
    }

    // number of points in the set
    public int size() {
        return this.n;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();

        // call recursive function
        this.root = insert(this.root, p, VERTICAL);
    }

    private Node insert(Node h, Point2D p, boolean div) {
        // insert node to end of tree
        if (h == null) {
            Node tmp = new Node(div, p);
            this.n++;
            return tmp;
        }

        double x = p.x();
        double y = p.y();
        double hx = h.point.x();
        double hy = h.point.y();

        if (h.divide == VERTICAL) {
            if (x > hx) h.right = insert(h.right, p, !h.divide);
            else if (x < hx) h.left = insert(h.left, p, !h.divide);
            else if (y != hy) h.right = insert(h.right, p, !h.divide);
        }
        if (h.divide == HORIZONTAL) {
            if (y > hy) h.right = insert(h.right, p, !h.divide);
            else if (y < hy) h.left = insert(h.left, p, !h.divide);
            else if (x != hx) h.right = insert(h.right, p, !h.divide);
        }

        return h;
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();

        return contains(this.root, p);
    }

    private boolean contains(Node h, Point2D p) {
        while (h != null) {
            if (h.divide == VERTICAL) {
                if (p.x() > h.point.x()) h = h.right;
                else if (p.x() < h.point.x()) h = h.left;
                else if (p.y() != h.point.y()) h = h.right;
                else return true;
            }
            else {
                if (p.y() > h.point.y()) h = h.right;
                else if (p.y() < h.point.y()) h = h.left;
                else if (p.x() != h.point.x()) h = h.right;
                else return true;
            }
        }
        return false;
    }

    // draw all points to standard draw
    public void draw() {
        // implement recursive draw function
        draw(root, 0.0, 0.0, 1.0, 1.0);
    }

    private void draw(Node h, double xmin, double ymin, double xmax, double ymax) {
        if (h == null) return;

        // draw the point
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        h.point.draw();

        // draw the vertical (red) or horizontal (blue) boundary
        if (h.divide == VERTICAL) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.setPenRadius();
            RectHV r = new RectHV(h.point.x(), ymin, h.point.x(), ymax);
            r.draw();
            draw(h.right, h.point.x(), ymin, xmax, ymax);
            draw(h.left, xmin, ymin, h.point.x(), ymax);
        }

        if (h.divide == HORIZONTAL) {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.setPenRadius();
            RectHV r = new RectHV(xmin, h.point.y(), xmax, h.point.y());
            r.draw();
            draw(h.right, xmin, h.point.y(), xmax, ymax);
            draw(h.left, xmin, ymin, xmax, h.point.y());
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();

        Stack<Point2D> points = new Stack<>();
        RectHV possible = new RectHV(0.0, 0.0, 1.0, 1.0);
        range(root, possible, rect, points);
        return points;
    }

    private void range(Node h, RectHV hr, RectHV r, Stack<Point2D> points) {
        if (h == null) return;
        if (!hr.intersects(r)) return;

        if (r.contains(h.point)) points.push(h.point);
        double xmin, ymin, xmax, ymax;
        if (h.divide == VERTICAL) {
            xmin = h.point.x();
            ymin = hr.ymin();
            xmax = hr.xmax();
            ymax = hr.ymax();

            range(h.right, new RectHV(xmin, ymin, xmax, ymax), r, points);

            xmin = hr.xmin();
            xmax = h.point.x();
            range(h.left, new RectHV(xmin, ymin, xmax, ymax), r, points);
        }
        else {
            xmin = hr.xmin();
            ymin = h.point.y();
            xmax = hr.xmax();
            ymax = hr.ymax();

            range(h.right, new RectHV(xmin, ymin, xmax, ymax), r, points);

            ymin = hr.ymin();
            ymax = h.point.y();
            range(h.left, new RectHV(xmin, ymin, xmax, ymax), r, points);
        }

    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (this.isEmpty()) return null;

        Node nearest = new Node(root.divide, root.point);
        nearest.right = root.right;
        nearest.left = root.left;
        RectHV r = new RectHV(0.0, 0.0, 1.0, 1.0);
        nearest(root, r, nearest, p);
        return nearest.point;
    }

    private void nearest(Node h, RectHV r, Node nearest, Point2D p) {
        if (h == null) return;
        if (p.distanceSquaredTo(h.point) < p.distanceSquaredTo(nearest.point)) {
            nearest.point = h.point;
        }

        double hx = h.point.x();
        double hy = h.point.y();
        double px = p.x();
        double py = p.y();
        double xmin, ymin, xmax, ymax;

        if (h.divide == VERTICAL) {
            ymin = r.ymin();
            ymax = r.ymax();
            xmin = hx;
            xmax = r.xmax();
            RectHV rt = new RectHV(xmin, ymin, xmax, ymax);

            xmin = r.xmin();
            xmax = hx;
            RectHV lb = new RectHV(xmin, ymin, xmax, ymax);

            if (px >= hx) {
                nearest(h.right, rt, nearest, p);
                if (lb.distanceSquaredTo(p) < nearest.point.distanceSquaredTo(p)) {
                    nearest(h.left, lb, nearest, p);
                }
            }
            else {
                nearest(h.left, lb, nearest, p);
                if (rt.distanceSquaredTo(p) < nearest.point.distanceSquaredTo(p)) {
                    nearest(h.right, rt, nearest, p);
                }
            }
        }
        else {
            xmin = r.xmin();
            xmax = r.xmax();
            ymin = hy;
            ymax = r.ymax();
            RectHV rt = new RectHV(xmin, ymin, xmax, ymax);

            ymin = r.ymin();
            ymax = hy;
            RectHV lb = new RectHV(xmin, ymin, xmax, ymax);

            if (py >= hy) {
                nearest(h.right, rt, nearest, p);
                if (lb.distanceSquaredTo(p) < nearest.point.distanceSquaredTo(p)) {
                    nearest(h.left, lb, nearest, p);
                }
            }
            else {
                nearest(h.left, lb, nearest, p);
                if (rt.distanceSquaredTo(p) < nearest.point.distanceSquaredTo(p)) {
                    nearest(h.right, rt, nearest, p);
                }
            }
        }
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        KdTree points = new KdTree();
        StdOut.println(points.isEmpty()); // true for input10.txt
        In in = new In(args[0]);
        double[] coords = in.readAllDoubles();
        StdOut.println(coords);
        for (int i = 0; i < coords.length; i += 2) {
            points.insert(new Point2D(coords[i], coords[i + 1]));
        }
        StdOut.println(points.isEmpty()); // false
        StdOut.println(points.size()); // 10
        StdOut.println(points.contains(new Point2D(0.372, 0.497))); // true
        StdOut.println(points.contains(new Point2D(0.372, 0.487))); // false
        StdOut.println(points.range(new RectHV(0.2, 0.2, 0.5, 0.5)));
        StdOut.println(points.nearest(new Point2D(0.1, 0.1)));
        points.draw();
    }

}
