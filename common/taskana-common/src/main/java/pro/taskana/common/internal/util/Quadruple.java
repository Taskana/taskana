package pro.taskana.common.internal.util;

public class Quadruple<A, S, D, F> {

  private final A first;
  private final S second;
  private final D third;
  private final F fourth;

  private Quadruple(A first, S second, D third, F fourth) {
    this.first = first;
    this.second = second;
    this.third = third;
    this.fourth = fourth;
  }

  public static <A, S, D, F> Quadruple<A, S, D, F> of(A a, S s, D d, F f) {
    return new Quadruple<>(a, s, d, f);
  }

  public A getFirst() {
    return first;
  }

  public S getSecond() {
    return second;
  }

  public D getThird() {
    return third;
  }

  public F getFourth() {
    return fourth;
  }
}
