package pro.taskana.common.internal.util;

import java.util.Objects;

public final class Pair<L, R> {

  private L left;

  private R right;

  public Pair(L left, R right) {
    this.left = left;
    this.right = right;
  }

  public Pair() {}

  public L getLeft() {
    return left;
  }

  public void setLeft(L left) {
    this.left = left;
  }

  public R getRight() {
    return right;
  }

  public void setRight(R right) {
    this.right = right;
  }

  public static <L, R> Pair<L, R> of(L left, R right) {
    return new Pair<>(left, right);
  }

  @Override
  public int hashCode() {
    return Objects.hash(left, right);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Pair<?, ?> other = (Pair<?, ?>) obj;
    return Objects.equals(left, other.left) && Objects.equals(right, other.right);
  }

  @Override
  public String toString() {
    return "Pair [left=" + left + ", right=" + right + "]";
  }
}
