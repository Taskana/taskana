package pro.taskana.task.internal.builder;

import java.time.Instant;

import pro.taskana.task.api.TaskState;
import pro.taskana.task.internal.models.TaskImpl;

class TaskTestImpl extends TaskImpl {

  private boolean freezeState = false;
  private boolean freezeCreated = false;
  private boolean freezeModified = false;
  private boolean freezeRead = false;
  private boolean freezeTransferred = false;

  @Override
  public void setState(TaskState state) {
    if (!freezeState) {
      super.setState(state);
    }
  }

  public void setStateIgnoreFreeze(TaskState state) {
    super.setState(state);
  }

  @Override
  public void setCreated(Instant created) {
    if (!freezeCreated) {
      super.setCreated(created);
    }
  }

  public void setCreatedIgnoreFreeze(Instant created) {
    super.setCreated(created);
  }

  @Override
  public void setModified(Instant modified) {
    if (!freezeModified) {
      super.setModified(modified);
    }
  }

  public void setModifiedIgnoreFreeze(Instant modified) {
    super.setModified(modified);
  }

  @Override
  public void setRead(boolean isRead) {
    if (!freezeRead) {
      super.setRead(isRead);
    }
  }

  public void setReadIgnoreFreeze(boolean isRead) {
    super.setRead(isRead);
  }

  @Override
  public void setTransferred(boolean isTransferred) {
    if (!freezeTransferred) {
      super.setTransferred(isTransferred);
    }
  }

  public void setTransferredIgnoreFreeze(boolean isTransferred) {
    super.setTransferred(isTransferred);
  }

  public void freezeState() {
    freezeState = true;
  }

  public void unfreezeState() {
    freezeState = false;
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

  public void freezeRead() {
    freezeRead = true;
  }

  public void unfreezeRead() {
    freezeRead = false;
  }

  public void freezeTransferred() {
    freezeTransferred = true;
  }

  public void unfreezeTransferred() {
    freezeTransferred = false;
  }
}
