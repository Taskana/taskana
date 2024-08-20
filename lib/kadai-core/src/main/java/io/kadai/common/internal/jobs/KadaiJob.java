package io.kadai.common.internal.jobs;

import io.kadai.common.api.exceptions.KadaiException;

/** Interface for all background KADAI jobs. */
public interface KadaiJob {

  /**
   * Execute the KadaiJob.
   *
   * @throws KadaiException if any exception occurs during the execution.
   */
  void run() throws KadaiException;
}
