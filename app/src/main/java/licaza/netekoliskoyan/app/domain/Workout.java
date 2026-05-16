package licaza.netekoliskoyan.app.domain;

import java.util.List;

public record Workout(String id, String user, String gym, String date, List<Exercise> exercises) {}
