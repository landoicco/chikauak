package licaza.netekoliskoyan.iac;

import java.util.Map;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionUrl;
import software.amazon.awscdk.services.lambda.FunctionUrlAuthType;
import software.amazon.awscdk.services.lambda.FunctionUrlOptions;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

public class IacStack extends Stack {
  public IacStack(final Construct scope, final String id, final StackProps props) {
    super(scope, id, props);

    Table workoutTable =
        Table.Builder.create(this, "WorkoutTable")
            .tableName("Workouts")
            .partitionKey(Attribute.builder().name("id").type(AttributeType.STRING).build())
            .billingMode(BillingMode.PAY_PER_REQUEST)
            .build();

    // Ruta relativa hacia el archivo JAR de tu aplicación de Spring
    String jarPath = "../netekoliskoyan.jar";

    // Configuración común para las Lambdas de Java
    // Nota: Le asignamos 512MB de RAM. Spring Boot necesita un mínimo de memoria para arrancar
    // bien.
    // El timeout es de 30 segundos debido al Cold Start (arranque en frío) inicial de Spring.
    Map<String, String> baseEnvironment = Map.of("SPRING_MAIN_LAZY_INITIALIZATION", "true");

    // 2. Lambda para Guardar Entrenamiento
    Function saveWorkoutLambda =
        Function.Builder.create(this, "SaveWorkoutLambda")
            .runtime(Runtime.JAVA_17)
            .handler(
                "org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest")
            .code(Code.fromAsset(jarPath))
            .timeout(Duration.seconds(30))
            .memorySize(512)
            .environment(
                Map.of(
                    "SPRING_CLOUD_FUNCTION_DEFINITION", "saveWorkout",
                    "SPRING_MAIN_LAZY_INITIALIZATION", "true"))
            .build();

    // 3. Lambda para Consultar Entrenamiento
    Function getWorkoutLambda =
        Function.Builder.create(this, "GetWorkoutByIdLambda")
            .runtime(Runtime.JAVA_17)
            .handler(
                "org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest")
            .code(Code.fromAsset(jarPath))
            .timeout(Duration.seconds(30))
            .memorySize(512)
            .environment(
                Map.of(
                    "SPRING_CLOUD_FUNCTION_DEFINITION", "getWorkoutById",
                    "SPRING_MAIN_LAZY_INITIALIZATION", "true"))
            .build();

    // 4. Otorgar permisos a las Lambdas sobre la tabla de DynamoDB
    workoutTable.grantWriteData(saveWorkoutLambda); // Permiso de escritura para guardar
    workoutTable.grantReadData(getWorkoutLambda); // Permiso de lectura para buscar

    // 1. URL pública para Guardar
    FunctionUrl saveUrl =
        saveWorkoutLambda.addFunctionUrl(
            FunctionUrlOptions.builder()
                .authType(FunctionUrlAuthType.NONE) // Pública sin contraseña para pruebas rápidas
                .build());

    // 2. URL pública para Consultar
    FunctionUrl getUrl =
        getWorkoutLambda.addFunctionUrl(
            FunctionUrlOptions.builder().authType(FunctionUrlAuthType.NONE).build());

    // 3. Imprimir los enlaces en tu terminal al terminar el despliegue
    CfnOutput.Builder.create(this, "SaveWorkoutUrl").value(saveUrl.getUrl()).build();

    CfnOutput.Builder.create(this, "GetWorkoutUrl").value(getUrl.getUrl()).build();
  }
}
