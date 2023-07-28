/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

public class PointSET {

    private SET<Point2D> points;

    // construct an empty set of points
    public PointSET() {
        points = new SET<>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return points.isEmpty();
    }

    // number of points in the set
    public int size() {
        return points.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (!points.contains(p)) points.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        return points.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D p : points) {
            p.draw();
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();
        Queue<Point2D> q = new Queue<>();
        for (Point2D p : points) {
            if (rect.contains(p)) q.enqueue(p);
        }
        return q;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (points.isEmpty()) return null;
        double dist = Double.POSITIVE_INFINITY;
        Point2D min = null;
        for (Point2D q : points) {
            if (q.distanceSquaredTo(p) < dist) {
                min = q;
                dist = q.distanceSquaredTo(p);
            }
        }
        return min;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        PointSET points = new PointSET();
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

