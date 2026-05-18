package licaza.chikauak.app.function;

import java.util.function.Function;
import licaza.chikauak.app.domain.Workout;
import licaza.chikauak.app.infrastructure.WorkoutRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkoutFunctions {

  private final WorkoutRepository repository;

  // Spring inyecta automáticamente nuestro repositorio aquí
  public WorkoutFunctions(WorkoutRepository repository) {
    this.repository = repository;
  }

  @Bean
  public Function<Workout, String> saveWorkout() {
    return workout -> {
      // Validación básica inicial
      if (workout.exercises() == null || workout.exercises().isEmpty()) {
        return "Error: El entrenamiento debe contener al menos un ejercicio.";
      }

      repository.save(workout);
      return "Workout '"
          + workout.id()
          + "' registrado con éxito para el usuario "
          + workout.user()
          + ".";
    };
  }

  @Bean
  public Function<String, Workout> getWorkoutById() {
    return id ->
        repository
            .findById(id)
            .orElse(new Workout(id, "unknown_user", "not_found", null, java.util.List.of()));
  }
}
