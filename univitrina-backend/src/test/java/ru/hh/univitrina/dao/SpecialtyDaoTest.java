package ru.hh.univitrina.dao;

import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hh.univitrina.UnivitrinaTestBase;
import ru.hh.univitrina.entity.Section;
import ru.hh.univitrina.entity.Specialty;
import ru.hh.univitrina.entity.TrainingDirection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.hh.univitrina.dao.CreateObjectsUtil.createSpecialty;
import static ru.hh.univitrina.dao.CreateObjectsUtil.createTrainingDirection;

public class SpecialtyDaoTest extends UnivitrinaTestBase {

  @Inject
  private SpecialtyDao specialtyDao;

  @BeforeEach
  private void init() {
    insertData();
  }

  @Test
  public void getSearchSuggestionByNameTest() {
    List<Specialty> expectedSpecialties = getExpectedList();
    List<Specialty> actualSpecialties = getSearchSuggestion("spa", 10);
    assertEqualsSpecialtyList(expectedSpecialties, actualSpecialties);
  }

  @Test
  public void getSearchSuggestionLimitTest() {
    List<Specialty> expectedSpecialties = getExpectedList().subList(0, 2);
    List<Specialty> actualSpecialties = getSearchSuggestion("spa", 2);
    assertEqualsSpecialtyList(expectedSpecialties, actualSpecialties);
  }

  private void insertData() {
    Section section1 = new Section("Section1");
    Section section2 = new Section("Section2");

    TrainingDirection tDirection1 = createTrainingDirection("tDirection1", "tDirectionCode1", section1);
    TrainingDirection tDirection2 = createTrainingDirection("tDirection2", "tDirectionCode2", section2);

    Specialty specialty1 = createSpecialty("SpA1", "02.03.01", tDirection1);
    Specialty specialty2 = createSpecialty("SpA2", "02.03.02", tDirection1);
    Specialty specialty3 = createSpecialty("SpA3", "02.03.03", tDirection1);
    Specialty specialty4 = createSpecialty("SpB1", "03.03.01", tDirection2);

    saveObjects(section1, section2, tDirection1, tDirection2, specialty1, specialty2, specialty3, specialty4);
  }

  private List<Specialty> getSearchSuggestion(String prefix, int limit) {
    return transactionalScope.read(() -> specialtyDao.getSearchSuggestion(prefix, limit));
  }

  private List<Specialty> getExpectedList() {
    Specialty specialty1 = createSpecialty(1, "SpA1", "02.03.01");
    Specialty specialty2 = createSpecialty(2, "SpA2", "02.03.02");
    Specialty specialty3 = createSpecialty(3, "SpA3", "02.03.03");
    return List.of(specialty1, specialty2, specialty3);
  }

  private boolean equalSpecialty(Specialty sp1, Specialty sp2) {
    return Objects.equals(sp1.getId(), sp2.getId())
        && Objects.equals(sp1.getName(), sp2.getName())
        && Objects.equals(sp1.getCode(), sp2.getCode());
  }

  private boolean contains(List<Specialty> specialtyList, Specialty specialty) {
    return specialtyList.stream()
        .anyMatch(sp -> equalSpecialty(sp, specialty));
  }

  private void assertEqualsSpecialtyList(List<Specialty> expectedList, List<Specialty> actualList) {
    assertEquals(expectedList.size(), actualList.size(), "Size of SpecialtyLists not equal: ");
    expectedList.forEach(specialty -> assertTrue(contains(actualList, specialty)));
  }
}