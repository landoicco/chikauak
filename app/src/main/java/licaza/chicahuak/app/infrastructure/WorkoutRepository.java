package licaza.chikauak.app.infrastructure;

import java.time.Instant;
import java.util.Optional;
import licaza.chikauak.app.domain.Workout;
import org.springframework.stereotype.Repository;

@Repository
public class WorkoutRepository {

  public void save(Workout workout) {
    // Simulación en consola. Aquí conectaremos el SDK de DynamoDB más adelante.
    System.out.println("[DynamoDB] Guardando entrenamiento con ID: " + workout.id());
  }

  public Optional<Workout> findById(String id) {
    // Simulación de búsqueda. Devuelve un objeto quemado en código para pruebas.
    System.out.println("[DynamoDB] Buscando entrenamiento con ID: " + id);

    Workout mockWorkout =
        new Workout(id, "user_test_123", "Push Day", Instant.now().toString(), java.util.List.of());

    return Optional.of(mockWorkout);
  }
}
