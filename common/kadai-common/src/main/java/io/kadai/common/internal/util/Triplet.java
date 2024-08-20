package io.kadai.common.internal.util;

public class Triplet<L, M, R> {

  private final L left;
  private final M middle;
  private final R right;

  private Triplet(L left, M middle, R right) {
    this.left = left;
    this.middle = middle;
    this.right = right;
  }

  public static <L, M, R> Triplet<L, M, R> of(L left, M middle, R right) {
    return new Triplet<>(left, middle, right);
  }

  public L getLeft() {
    return left;
  }

  public M getMiddle() {
    return middle;
  }

  public R getRight() {
    return right;
  }
}
