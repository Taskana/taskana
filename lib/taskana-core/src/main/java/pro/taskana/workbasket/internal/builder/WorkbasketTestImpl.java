package pro.taskana.workbasket.internal.builder;

import java.time.Instant;

import pro.taskana.workbasket.internal.models.WorkbasketImpl;

class WorkbasketTestImpl extends WorkbasketImpl {

  private boolean freezeCreated = false;
  private boolean freezeModified = false;

  @Override
  public void setCreated(Instant created) {
    if (!freezeCreated) {
      super.setCreated(created);
    }
  }

  public void setCreatedIgnoringFreeze(Instant created) {
    super.setCreated(created);
  }

  @Override
  public void setModified(Instant modified) {
    if (!freezeModified) {
      super.setModified(modified);
    }
  }

  public void setModifiedIgnoringFreeze(Instant modified) {
    super.setModified(modified);
  }

  public void freezeCreated() {
    freezeCreated = true;
  }

  public void unfreezeCreated() {
    freezeCreated = false;
  }

  public void freezeModified() {
    freezeModified = true;
  }

  public void unfreezeModified() {
    freezeModified = false;
  }
}
