package dk.kb.stream;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * <p>Helper class for having multiple values in a stream. </p>
 *
 * @noinspection WeakerAccess
 */
public class StreamTuple<L, R> implements Comparable<StreamTuple<L, R>> {

    protected final L left;
    protected final R right;

    /**
     * for {@code (l, r) -> new StreamTuple<>(l, r) }
     *
     * @param left  leftmost item in tuple. Must be non-null.
     * @param right rightmost item in tuple. Must be non-null.
     */

    public StreamTuple(L left, R right) {
        this.left = Objects.requireNonNull(left, "left");
        this.right = Objects.requireNonNull(right, "right");
    }

    /**
     * Suitable for Stream::create.  Both left and right are set to the value passed in.  This is a good start if you have
     * a stream of keys which you need later.
     *
     * @param item the item to be placed in both {@code left} and {@code right}
     * @return StreamTuple with item in both left and right.
     */

    public static <I> StreamTuple<I, I> create(I item) {
        return new StreamTuple<>(item, item);
    }

    /**
     * Gets the left value.
     *
     * @return L left
     */
    public L left() {
        return left;
    }

    /**
     * Gets the right value
     *
     * @return R right
     */
    public R right() {
        return right;
    }

    /**
     * Return new TupleElement object with the given value (which may
     * be a completely different type than the one hold by this
     * object) and the same context.
     *
     * @param right value for new object.
     * @return IdValue with the same id and the new value.
     */
    public <U> StreamTuple<L, U> of(U right) {
        return new StreamTuple<>(left, right);
    }

    /**
     * Apply a given function to the value and return a new IdValue
     * object with the result (and same context).
     *
     * @param f function to apply to the current value to get the new value.
     * @return new SteamTuple with the same id and the result of applying f to current value.
     */
    public <U> StreamTuple<L, U> map(Function<R, U> f) {
        return of(f.apply(right));
    }

    /**
     * Apply a given two-argument function to the context <b>and</b>
     * the current value, and return a new object with the result (and
     * the same context).
     *
     * @param f function to apply to context and value to get new value.
     * @return new IdValue with the same id and the result of applying
     * f to current id and value.
     */
    public <U> StreamTuple<L, U> map(BiFunction<L, R, U> f) {
        return of(f.apply(left, right));
    }

    /**
     * for <pre>.filter(st -> st.filter(r -> ...))</pre>
     */
    public boolean filter(Predicate<R> predicate) {
        return predicate.test(right);
    }

    /**
     * for <pre>.filter(st -> st.filter((l, r) -> ...))</pre>
     */
    public boolean filter(BiPredicate<L, R> predicate) {
        return predicate.test(left, right);
    }

    /**
     * for <pre>.flatMap(st -> st.flatMap(r -> ....))</pre>
     */

    public <U> Stream<StreamTuple<L, U>> flatMap(Function<R, Stream<U>> f) {
        return f.apply(right).map(this::of);
    }

    /**
     * for <pre>.flatMap(st -> st.flatMap((l, r) -> ....))</pre>
     */
    public <U> Stream<StreamTuple<L, U>> flatMap(BiFunction<L, R, Stream<U>> f) {
        return f.apply(left, right).map(this::of);
    }

    /**
     * for <pre>.peek(st -> st.peek(r -> ....))</pre> for cases where it makes more sense than
     * just referring to st.left() and st.right() directly.
     * @param f
     */

    public void peek(Consumer<R> f) {
        f.accept(right);
    }

    /**
     * for <pre>.peek(st -> st.peek((l,r) -> ....))</pre> for cases where it makes more sense than
     * just referring to st.left() and st.right() directly.
     */

    public void peek(BiConsumer<L, R> f) {
        f.accept(left, right);
    }

    /**
     * Default toString() as generated by IntelliJ
     */
    @Override
    public String toString() {
        return "StreamTuple{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }

    /**
     * Make StreamTuples comparable.  The right value is considered first, and then the key.
     * It is not required that they are comparable so this may fail with a ClassCastException.
     *
     * @noinspection unchecked
     */
    @Override
    public int compareTo(StreamTuple<L, R> that) {
        int i;
        Comparable<R> cr = (Comparable<R>) right;
        i = cr.compareTo(that.right);
        if (i == 0) {
            Comparable<L> cl = (Comparable<L>) left;
            i = cl.compareTo(that.left);
        }
        return i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StreamTuple<?, ?> that = (StreamTuple<?, ?>) o;
        return Objects.equals(left, that.left) &&
                Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}
