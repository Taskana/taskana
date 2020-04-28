package pro.taskana.ldap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pro.taskana.rest.resource.AccessIdRepresentationModel;

/**
 * Implementation of LdapCache used for Unit tests.
 *
 * @author bbr
 */
public class LdapCacheTestImpl implements LdapCache {

  private final List<AccessIdRepresentationModel> accessIds =
      new ArrayList<>(
          Arrays.asList(
              new AccessIdRepresentationModel("Martin, Rojas Miguel Angel", "user_1_1"),
              new AccessIdRepresentationModel("Zorgati, Mustapha", "user_2_1"),
              new AccessIdRepresentationModel("Behrendt, Maximilian", "max"),
              new AccessIdRepresentationModel("Bert, Ali", "teamlead_5"),
              new AccessIdRepresentationModel("Hagen, Holger", "teamlead_3"),
              new AccessIdRepresentationModel("Breier, Bernd", "user_2_2"),
              new AccessIdRepresentationModel("Fielmalz, Anke", "user017"),
              new AccessIdRepresentationModel("Mente, Maximilian", "max_mente"),
              new AccessIdRepresentationModel("Theke, Bernd", "user_2_3"),
              new AccessIdRepresentationModel("Ferrante, Elena", "elena"),
              new AccessIdRepresentationModel("Mueller, Simone", "simone"),
              new AccessIdRepresentationModel("Sirup, Aaron", "user001"),
              new AccessIdRepresentationModel("Nacho, recuerda", "user_1_2"),
              new AccessIdRepresentationModel("Lass, Ada", "user003"),
              new AccessIdRepresentationModel("Tion, Addi", "user004"),
              new AccessIdRepresentationModel("Lette, Adi", "user005"),
              new AccessIdRepresentationModel("Admin", "teamlead_2"),
              new AccessIdRepresentationModel("Native, Alter", "user006"),
              new AccessIdRepresentationModel("Herum, Albert", "user007"),
              new AccessIdRepresentationModel("Meyer, Dominik", "teamlead_1"),
              new AccessIdRepresentationModel("Mente, Ali", "user009"),
              new AccessIdRepresentationModel("Nach, Alma", "user011"),
              new AccessIdRepresentationModel("Gehzauch, Anders", "user012"),
              new AccessIdRepresentationModel("Theke, Andi", "user013"),
              new AccessIdRepresentationModel("Kreuz, Andreas", "user014"),
              new AccessIdRepresentationModel("Tiefsee, Anka", "user016"),
              new AccessIdRepresentationModel("Fassen, Ann", "user018"),
              new AccessIdRepresentationModel("Probe, Ann", "user019"),
              new AccessIdRepresentationModel("Bolika, Anna", "user020"),
              new AccessIdRepresentationModel("Ecke, Anna", "user021"),
              new AccessIdRepresentationModel("Hosi, Anna", "user022"),
              new AccessIdRepresentationModel("Kronis-Tisch, Anna", "user023"),
              new AccessIdRepresentationModel("Logie, Anna", "user024"),
              new AccessIdRepresentationModel("Luehse, Anna", "user025"),
              new AccessIdRepresentationModel("Nass, Anna", "user026"),
              new AccessIdRepresentationModel("Thalb, Anna", "user027"),
              new AccessIdRepresentationModel("Tomie, Anna", "user028"),
              new AccessIdRepresentationModel("Donnich, Anne", "user029"),
              new AccessIdRepresentationModel("Kaffek, Anne", "user030"),
              new AccessIdRepresentationModel("Thek, Anne", "user031"),
              new AccessIdRepresentationModel("Matoer, Anni", "user032"),
              new AccessIdRepresentationModel("Ragentor, Ansgar", "user033"),
              new AccessIdRepresentationModel("Stoteles, Ari", "user034"),
              new AccessIdRepresentationModel("Thmetik, Ari", "user035"),
              new AccessIdRepresentationModel("Nuehm, Arno", "user036"),
              new AccessIdRepresentationModel("Schocke, Artie", "user037"),
              new AccessIdRepresentationModel("Stoppel, Bart", "user038"),
              new AccessIdRepresentationModel("Beitung, Bea", "user039"),
              new AccessIdRepresentationModel("Ildich, Bea", "user040"),
              new AccessIdRepresentationModel("Vista, Bella", "user041"),
              new AccessIdRepresentationModel("Utzer, Ben", "user042"),
              new AccessIdRepresentationModel("Zien, Ben", "user043"),
              new AccessIdRepresentationModel("Stein, Bernd", "user044"),
              new AccessIdRepresentationModel("Deramen, Bill", "user045"),
              new AccessIdRepresentationModel("Honig, Bine", "user046"),
              new AccessIdRepresentationModel("Densatz, Bo", "user047"),
              new AccessIdRepresentationModel("Densee, Bo", "user048"),
              new AccessIdRepresentationModel("Lerwagen, Bo", "user049"),
              new AccessIdRepresentationModel("Tail, Bob", "user050"),
              new AccessIdRepresentationModel("Ketta, Bruce", "user051"),
              new AccessIdRepresentationModel("Terrie, Bud", "user052"),
              new AccessIdRepresentationModel("Biener-Haken, Cara", "user053"),
              new AccessIdRepresentationModel("Ass, Caro", "user054"),
              new AccessIdRepresentationModel("Kaffee, Caro", "user055"),
              new AccessIdRepresentationModel("Linger, Caro", "user056"),
              new AccessIdRepresentationModel("tenSaft, Caro", "user057"),
              new AccessIdRepresentationModel("Antheme, Chris", "user058"),
              new AccessIdRepresentationModel("Baum, Chris", "user059"),
              new AccessIdRepresentationModel("Tall, Chris", "user060"),
              new AccessIdRepresentationModel("Reiniger, Claas", "user061"),
              new AccessIdRepresentationModel("Grube, Claire", "user062"),
              new AccessIdRepresentationModel("Fall, Clara", "user063"),
              new AccessIdRepresentationModel("Korn, Clara", "user064"),
              new AccessIdRepresentationModel("Lenriff, Cora", "user065"),
              new AccessIdRepresentationModel("Schiert, Cora", "user066"),
              new AccessIdRepresentationModel("Hose, Cord", "user067"),
              new AccessIdRepresentationModel("Onbleu, Cord", "user068"),
              new AccessIdRepresentationModel("Umkleide, Damon", "user069"),
              new AccessIdRepresentationModel("Affier, Dean", "user070"),
              new AccessIdRepresentationModel("Orm, Dean", "user071"),
              new AccessIdRepresentationModel("Platz, Dennis", "user072"),
              new AccessIdRepresentationModel("Milch, Dick", "user073"),
              new AccessIdRepresentationModel("Mow, Dina", "user074"),
              new AccessIdRepresentationModel("Keil, Donna", "user075"),
              new AccessIdRepresentationModel("Littchen, Donna", "user076"),
              new AccessIdRepresentationModel("Wetter, Donna", "user077"),
              new AccessIdRepresentationModel("Was, Ed", "user078"),
              new AccessIdRepresentationModel("Khar, Ede", "user079"),
              new AccessIdRepresentationModel("Nut, Ella", "user080"),
              new AccessIdRepresentationModel("Stisch, Ella", "user081"),
              new AccessIdRepresentationModel("Diel, Emma", "user082"),
              new AccessIdRepresentationModel("Herdamit, Emma", "user083"),
              new AccessIdRepresentationModel("Mitter-Uhe, Emma", "user084"),
              new AccessIdRepresentationModel("Tatt, Erich", "user085"),
              new AccessIdRepresentationModel("Drigend, Ernie", "user086"),
              new AccessIdRepresentationModel("Poly, Esther", "user087"),
              new AccessIdRepresentationModel("Trautz, Eugen", "user088"),
              new AccessIdRepresentationModel("Quiert, Eva", "user089"),
              new AccessIdRepresentationModel("Inurlaub, Fatma", "user090"),
              new AccessIdRepresentationModel("Land, Finn", "user091"),
              new AccessIdRepresentationModel("Sternis, Finn", "user092"),
              new AccessIdRepresentationModel("Furt, Frank", "user093"),
              new AccessIdRepresentationModel("Reich, Frank", "user094"),
              new AccessIdRepresentationModel("Iskaner, Franz", "user095"),
              new AccessIdRepresentationModel("Nerr, Franziska", "user096"),
              new AccessIdRepresentationModel("Zafen, Friedrich", "user097"),
              new AccessIdRepresentationModel("Pomm, Fritz", "user098"),
              new AccessIdRepresentationModel("deWegs, Gera", "user099"),
              new AccessIdRepresentationModel("Staebe, Gitta", "user100"),
              new AccessIdRepresentationModel("Zend, Glenn", "user101"),
              new AccessIdRepresentationModel("Fisch, Grete", "user102"),
              new AccessIdRepresentationModel("Zucker, Gus", "user103"),
              new AccessIdRepresentationModel("Muhn, Hanni", "user104"),
              new AccessIdRepresentationModel("Fermesse, Hanno", "user105"),
              new AccessIdRepresentationModel("Aplast, Hans", "user106"),
              new AccessIdRepresentationModel("Eart, Hans", "user107"),
              new AccessIdRepresentationModel("Back, Hardy", "user108"),
              new AccessIdRepresentationModel("Beau, Harry", "user109"),
              new AccessIdRepresentationModel("Kraut, Heide", "user110"),
              new AccessIdRepresentationModel("Witzka, Heide", "user111"),
              new AccessIdRepresentationModel("Buchen, Hein", "user112"),
              new AccessIdRepresentationModel("Lichkeit, Hein", "user113"),
              new AccessIdRepresentationModel("Suchung, Hein", "user114"),
              new AccessIdRepresentationModel("Ellmann, Heinz", "user115"),
              new AccessIdRepresentationModel("Ketchup, Heinz", "user116"),
              new AccessIdRepresentationModel("Zeim, Hilde", "user117"),
              new AccessIdRepresentationModel("Bilien, Immo", "user118"),
              new AccessIdRepresentationModel("Her, Inge", "user119"),
              new AccessIdRepresentationModel("Wahrsam, Inge", "user120"),
              new AccessIdRepresentationModel("Flamm, Ingo", "user121"),
              new AccessIdRepresentationModel("Enzien, Ingrid", "user122"),
              new AccessIdRepresentationModel("Rohsch, Inken", "user123"),
              new AccessIdRepresentationModel("Ihr, Insa", "user124"),
              new AccessIdRepresentationModel("Nerda, Iska", "user125"),
              new AccessIdRepresentationModel("Eitz, Jens", "user126"),
              new AccessIdRepresentationModel("Nastik, Jim", "user127"),
              new AccessIdRepresentationModel("Gurt, Jo", "user128"),
              new AccessIdRepresentationModel("Kurrth, Jo", "user129"),
              new AccessIdRepresentationModel("Kolade, Joe", "user130"),
              new AccessIdRepresentationModel("Iter, Johann", "user131"),
              new AccessIdRepresentationModel("Tick, Joyce", "user132"),
              new AccessIdRepresentationModel("Case, Justin", "user133"),
              new AccessIdRepresentationModel("Time, Justin", "user134"),
              new AccessIdRepresentationModel("Komp, Jutta", "user135"),
              new AccessIdRepresentationModel("Mauer, Kai", "user136"),
              new AccessIdRepresentationModel("Pirinja, Kai", "user137"),
              new AccessIdRepresentationModel("Serpfalz, Kai", "user138"),
              new AccessIdRepresentationModel("Auer, Karl", "user139"),
              new AccessIdRepresentationModel("Ielauge, Karl", "user140"),
              new AccessIdRepresentationModel("Ifornjen, Karl", "user141"),
              new AccessIdRepresentationModel("Radi, Karl", "user142"),
              new AccessIdRepresentationModel("Verti, Karl", "user143"),
              new AccessIdRepresentationModel("Sery, Karo", "user144"),
              new AccessIdRepresentationModel("Lisator, Katha", "user145"),
              new AccessIdRepresentationModel("Flo, Kati", "user146"),
              new AccessIdRepresentationModel("Schenn, Knut", "user147"),
              new AccessIdRepresentationModel("Achse, Kurt", "user148"),
              new AccessIdRepresentationModel("Zepause, Kurt", "user149"),
              new AccessIdRepresentationModel("Zerr, Kurt", "user150"),
              new AccessIdRepresentationModel("Reden, Lasse", "user151"),
              new AccessIdRepresentationModel("Metten, Lee", "user152"),
              new AccessIdRepresentationModel("Arm, Lene", "user153"),
              new AccessIdRepresentationModel("Thur, Linnea", "user154"),
              new AccessIdRepresentationModel("Bonn, Lisa", "user155"),
              new AccessIdRepresentationModel("Sembourg, Luc", "user156"),
              new AccessIdRepresentationModel("Rung, Lucky", "user157"),
              new AccessIdRepresentationModel("Zafen, Ludwig", "user158"),
              new AccessIdRepresentationModel("Hauden, Lukas", "user159"),
              new AccessIdRepresentationModel("Hose, Lutz", "user160"),
              new AccessIdRepresentationModel("Tablette, Lutz", "user161"),
              new AccessIdRepresentationModel("Fehr, Luzie", "user162"),
              new AccessIdRepresentationModel("Nalyse, Magda", "user163"),
              new AccessIdRepresentationModel("Ehfer, Maik", "user164"),
              new AccessIdRepresentationModel("Sehr, Malte", "user165"),
              new AccessIdRepresentationModel("Thon, Mara", "user166"),
              new AccessIdRepresentationModel("Quark, Marga", "user167"),
              new AccessIdRepresentationModel("Nade, Marie", "user168"),
              new AccessIdRepresentationModel("Niert, Marie", "user169"),
              new AccessIdRepresentationModel("Neese, Mario", "user170"),
              new AccessIdRepresentationModel("Nette, Marion", "user171"),
              new AccessIdRepresentationModel("Nesium, Mark", "user172"),
              new AccessIdRepresentationModel("Thalle, Mark", "user173"),
              new AccessIdRepresentationModel("Diven, Marle", "user174"),
              new AccessIdRepresentationModel("Fitz, Marle", "user175"),
              new AccessIdRepresentationModel("Pfahl, Marta", "user176"),
              new AccessIdRepresentationModel("Zorn, Martin", "user177"),
              new AccessIdRepresentationModel("Krissmes, Mary", "user178"),
              new AccessIdRepresentationModel("Jess, Matt", "user179"),
              new AccessIdRepresentationModel("Strammer, Max", "user180"),
              new AccessIdRepresentationModel("Mumm, Maxi", "user181"),
              new AccessIdRepresentationModel("Morphose, Meta", "user182"),
              new AccessIdRepresentationModel("Uh, Mia", "user183"),
              new AccessIdRepresentationModel("Rofon, Mike", "user184"),
              new AccessIdRepresentationModel("Rosoft, Mike", "user185"),
              new AccessIdRepresentationModel("Liter, Milli", "user186"),
              new AccessIdRepresentationModel("Thär, Milli", "user187"),
              new AccessIdRepresentationModel("Welle, Mirko", "user188"),
              new AccessIdRepresentationModel("Thorat, Mo", "user189"),
              new AccessIdRepresentationModel("Thor, Moni", "user190"),
              new AccessIdRepresentationModel("Kinolta, Monika", "user191"),
              new AccessIdRepresentationModel("Mundhaar, Monika", "user192"),
              new AccessIdRepresentationModel("Munter, Monika", "user193"),
              new AccessIdRepresentationModel("Zwerg, Nat", "user194"),
              new AccessIdRepresentationModel("Elmine, Nick", "user195"),
              new AccessIdRepresentationModel("Thien, Niko", "user196"),
              new AccessIdRepresentationModel("Pferd, Nils", "user197"),
              new AccessIdRepresentationModel("Lerweise, Norma", "user198"),
              new AccessIdRepresentationModel("Motor, Otto", "user199"),
              new AccessIdRepresentationModel("Totol, Otto", "user200"),
              new AccessIdRepresentationModel("Nerr, Paula", "user201"),
              new AccessIdRepresentationModel("Imeter, Peer", "user202"),
              new AccessIdRepresentationModel("Serkatze, Peer", "user203"),
              new AccessIdRepresentationModel("Gogisch, Peter", "user204"),
              new AccessIdRepresentationModel("Silje, Peter", "user205"),
              new AccessIdRepresentationModel("Harmonie, Phil", "user206"),
              new AccessIdRepresentationModel("Ihnen, Philip", "user207"),
              new AccessIdRepresentationModel("Uto, Pia", "user208"),
              new AccessIdRepresentationModel("Kothek, Pina", "user209"),
              new AccessIdRepresentationModel("Zar, Pit", "user210"),
              new AccessIdRepresentationModel("Zeih, Polly", "user211"),
              new AccessIdRepresentationModel("Tswan, Puh", "user212"),
              new AccessIdRepresentationModel("Zufall, Rainer", "user213"),
              new AccessIdRepresentationModel("Lien, Rita", "user214"),
              new AccessIdRepresentationModel("Held, Roman", "user215"),
              new AccessIdRepresentationModel("Haar, Ross", "user216"),
              new AccessIdRepresentationModel("Dick, Roy", "user217"),
              new AccessIdRepresentationModel("Enplaner, Ruth", "user218"),
              new AccessIdRepresentationModel("Kommen, Ryan", "user219"),
              new AccessIdRepresentationModel("Philo, Sophie", "user220"),
              new AccessIdRepresentationModel("Matisier, Stig", "user221"),
              new AccessIdRepresentationModel("Loniki, Tessa", "user222"),
              new AccessIdRepresentationModel("Tralisch, Thea", "user223"),
              new AccessIdRepresentationModel("Logie, Theo", "user224"),
              new AccessIdRepresentationModel("Ister, Thorn", "user225"),
              new AccessIdRepresentationModel("Buktu, Tim", "user226"),
              new AccessIdRepresentationModel("Ate, Tom", "user227"),
              new AccessIdRepresentationModel("Pie, Udo", "user228"),
              new AccessIdRepresentationModel("Aloe, Vera", "user229"),
              new AccessIdRepresentationModel("Hausver, Walter", "user230"),
              new AccessIdRepresentationModel("Schuh, Wanda", "user231"),
              new AccessIdRepresentationModel("Rahm, Wolf", "user232"),
              new AccessIdRepresentationModel(
                  "businessadmin", "cn=businessadmin,ou=groups,o=taskanatest"),
              new AccessIdRepresentationModel(
                  "UsersGroup", "cn=usersgroup,ou=groups,o=taskanatest"),
              new AccessIdRepresentationModel(
                  "DevelopersGroup", "cn=developersgroup,ou=groups,o=taskanatest"),
              new AccessIdRepresentationModel(
                  "businessadmin", "cn=customersgroup,ou=groups,o=taskanatest"),
              new AccessIdRepresentationModel(
                  "user_domain_A", "cn=user_domain_a,ou=groups,o=taskanatest"),
              new AccessIdRepresentationModel("monitor", "cn=monitor,ou=groups,o=taskanatest"),
              new AccessIdRepresentationModel(
                  "user_domain_C", "cn=user_domain_c,ou=groups,o=taskanatest"),
              new AccessIdRepresentationModel(
                  "user_domain_D", "cn=user_domain_d,ou=groups,o=taskanatest"),
              new AccessIdRepresentationModel("admin", "cn=admin,ou=groups,o=taskanatest"),
              new AccessIdRepresentationModel(
                  "manager_domain_B", "cn=manager_domain_b,ou=groups,o=taskanatest"),
              new AccessIdRepresentationModel(
                  "manager_domain_C", "cn=manager_domain_c,ou=groups,o=taskanatest"),
              new AccessIdRepresentationModel(
                  "manager_domain_D", "cn=manager_domain_d,ou=groups,o=taskanatest"),
              new AccessIdRepresentationModel(
                  "teamlead_2", "cn=teamlead_2" + ",ou=groups,o=taskanatest"),
              new AccessIdRepresentationModel(
                  "teamlead_4", "cn=teamlead_4" + ",ou=groups,o=taskanatest"),
              new AccessIdRepresentationModel("team_3", "cn=team_3" + ",ou=groups,o=taskanatest"),
              new AccessIdRepresentationModel("team_4", "cn=team_4" + ",ou=groups,o=taskanatest")));
  /**
   * Dictionary is a {@link Map} collection that contains {@link AccessIdRepresentationModel} as key
   * (user) and {@link List} as value (groups of which the user is a member) .
   */
  private Map<AccessIdRepresentationModel, List<AccessIdRepresentationModel>> users;

  @Override
  public List<AccessIdRepresentationModel> findMatchingAccessId(
      String searchFor, int maxNumberOfReturnedAccessIds) {
    return findAcessIdResource(searchFor, maxNumberOfReturnedAccessIds, false);
  }

  @Override
  public List<AccessIdRepresentationModel> findGroupsOfUser(
      String searchFor, int maxNumberOfReturnedAccessIds) {
    if (users == null) {
      addUsersToGroups();
    }
    return findAcessIdResource(searchFor, maxNumberOfReturnedAccessIds, true);
  }

  @Override
  public List<AccessIdRepresentationModel> validateAccessId(String accessId) {
    return accessIds.stream()
        .filter(t -> (t.getAccessId().equalsIgnoreCase(accessId.toLowerCase())))
        .collect(Collectors.toList());
  }

  private List<AccessIdRepresentationModel> findAcessIdResource(
      String searchFor, int maxNumberOfReturnedAccessIds, boolean groupMember) {
    List<AccessIdRepresentationModel> usersAndGroups =
        accessIds.stream()
            .filter(
                t ->
                    (t.getName().toLowerCase().contains(searchFor.toLowerCase())
                        || t.getAccessId().toLowerCase().contains(searchFor.toLowerCase())))
            .collect(Collectors.toList());

    List<AccessIdRepresentationModel> usersAndGroupsAux = new ArrayList<>(usersAndGroups);
    if (groupMember) {
      usersAndGroupsAux.forEach(
          item -> {
            if (users.get(item) != null) {
              usersAndGroups.addAll(users.get(item));
            }
          });
    }

    usersAndGroups.sort(
        (AccessIdRepresentationModel a, AccessIdRepresentationModel b) -> a.getAccessId()
                                                                              .compareToIgnoreCase(
                                                                                  b.getAccessId()));

    return usersAndGroups.subList(0, Math.min(usersAndGroups.size(), maxNumberOfReturnedAccessIds));
  }

  private void addUsersToGroups() {
    List<AccessIdRepresentationModel> groups = new ArrayList<>();
    users = new HashMap<>();

    accessIds.forEach(
        item -> {
          if (!item.getAccessId().contains("ou=groups")) {
            users.put(item, new ArrayList<>());
          } else {
            groups.add(item);
          }
        });

    int groupNumber = 0;
    List<AccessIdRepresentationModel> group0 = new ArrayList<>();
    List<AccessIdRepresentationModel> group1 = new ArrayList<>();
    List<AccessIdRepresentationModel> group2 = new ArrayList<>();
    List<AccessIdRepresentationModel> group3 = new ArrayList<>();

    for (AccessIdRepresentationModel group : groups) {
      switch (groupNumber) {
        case 0:
          group0.add(group);
          break;
        case 1:
          group1.add(group);
          break;
        case 2:
          group2.add(group);
          break;
        case 3:
          group3.add(group);
          break;
        default:
          break;
      }
      groupNumber = (groupNumber + 1) % 4;
    }

    int countUser = 0;
    for (AccessIdRepresentationModel item : accessIds) {
      if (!item.getAccessId().contains("ou=groups")) {
        switch (countUser) {
          case 0:
            users.put(item, group0);
            break;
          case 1:
            users.put(item, group1);
            break;
          case 2:
            users.put(item, group2);
            break;
          case 3:
            users.put(item, group3);
            break;
          default:
            break;
        }
      }
      countUser = (countUser + 1) % 4;
    }
  }
}
