package team.aura_dev.lib.multiplatformcore.testcode.example;

import lombok.experimental.UtilityClass;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;

@UtilityClass
public class ConfigurateTest {
  public static void configurateTest() {
    final ConfigurationNode rootNode = SimpleCommentedConfigurationNode.root();
    final ConfigurationNode testNode = rootNode.getNode("test");

    testNode.setValue("Hi <3");

    assert "Hi <3".equals(testNode.getString());
  }
}
