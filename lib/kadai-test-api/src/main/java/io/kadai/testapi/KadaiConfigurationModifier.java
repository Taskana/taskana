package io.kadai.testapi;

import io.kadai.KadaiConfiguration;

public interface KadaiConfigurationModifier {

  KadaiConfiguration.Builder modify(KadaiConfiguration.Builder builder);
}
