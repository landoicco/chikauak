package licaza.chikauak.iac;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class IacApp {
  public static void main(final String[] args) {
    App app = new App();

    // Force deploy to us-east-1
    Environment env =
        Environment.builder()
            .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
            .region("us-east-1")
            .build();

    new IacStack(app, "ChikauakStack", StackProps.builder().env(env).build());

    app.synth();
  }
}
