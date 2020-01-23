package pro.taskana.ldap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pro.taskana.rest.resource.AccessIdResource;

/**
 * Implementation of LdapCache used for Unit tests.
 *
 * @author bbr
 */
public class LdapCacheTestImpl implements LdapCache {

  /**
   * Dictionary is a {@link Map} collection that contains {@link AccessIdResource} as key (user) and
   * {@link List} as value (groups of which the user is a member) .
   */
  private Map<AccessIdResource, List<AccessIdResource>> users;

  private List<AccessIdResource> accessIds =
      new ArrayList<>(
          Arrays.asList(
              new AccessIdResource("Martin, Rojas Miguel Angel", "user_1_1"),
              new AccessIdResource("Zorgati, Mustapha", "user_2_1"),
              new AccessIdResource("Behrendt, Maximilian", "max"),
              new AccessIdResource("Bert, Ali", "teamlead_5"),
              new AccessIdResource("Hagen, Holger", "teamlead_3"),
              new AccessIdResource("Breier, Bernd", "user_2_2"),
              new AccessIdResource("Fielmalz, Anke", "user017"),
              new AccessIdResource("Mente, Maximilian", "max_mente"),
              new AccessIdResource("Theke, Bernd", "user_2_3"),
              new AccessIdResource("Ferrante, Elena", "elena"),
              new AccessIdResource("Mueller, Simone", "simone"),
              new AccessIdResource("Sirup, Aaron", "user001"),
              new AccessIdResource("Nacho, recuerda", "user_1_2"),
              new AccessIdResource("Lass, Ada", "user003"),
              new AccessIdResource("Tion, Addi", "user004"),
              new AccessIdResource("Lette, Adi", "user005"),
              new AccessIdResource("Admin", "teamlead_2"),
              new AccessIdResource("Native, Alter", "user006"),
              new AccessIdResource("Herum, Albert", "user007"),
              new AccessIdResource("Meyer, Dominik", "teamlead_1"),
              new AccessIdResource("Mente, Ali", "user009"),
              new AccessIdResource("Nach, Alma", "user011"),
              new AccessIdResource("Gehzauch, Anders", "user012"),
              new AccessIdResource("Theke, Andi", "user013"),
              new AccessIdResource("Kreuz, Andreas", "user014"),
              new AccessIdResource("Tiefsee, Anka", "user016"),
              new AccessIdResource("Fassen, Ann", "user018"),
              new AccessIdResource("Probe, Ann", "user019"),
              new AccessIdResource("Bolika, Anna", "user020"),
              new AccessIdResource("Ecke, Anna", "user021"),
              new AccessIdResource("Hosi, Anna", "user022"),
              new AccessIdResource("Kronis-Tisch, Anna", "user023"),
              new AccessIdResource("Logie, Anna", "user024"),
              new AccessIdResource("Luehse, Anna", "user025"),
              new AccessIdResource("Nass, Anna", "user026"),
              new AccessIdResource("Thalb, Anna", "user027"),
              new AccessIdResource("Tomie, Anna", "user028"),
              new AccessIdResource("Donnich, Anne", "user029"),
              new AccessIdResource("Kaffek, Anne", "user030"),
              new AccessIdResource("Thek, Anne", "user031"),
              new AccessIdResource("Matoer, Anni", "user032"),
              new AccessIdResource("Ragentor, Ansgar", "user033"),
              new AccessIdResource("Stoteles, Ari", "user034"),
              new AccessIdResource("Thmetik, Ari", "user035"),
              new AccessIdResource("Nuehm, Arno", "user036"),
              new AccessIdResource("Schocke, Artie", "user037"),
              new AccessIdResource("Stoppel, Bart", "user038"),
              new AccessIdResource("Beitung, Bea", "user039"),
              new AccessIdResource("Ildich, Bea", "user040"),
              new AccessIdResource("Vista, Bella", "user041"),
              new AccessIdResource("Utzer, Ben", "user042"),
              new AccessIdResource("Zien, Ben", "user043"),
              new AccessIdResource("Stein, Bernd", "user044"),
              new AccessIdResource("Deramen, Bill", "user045"),
              new AccessIdResource("Honig, Bine", "user046"),
              new AccessIdResource("Densatz, Bo", "user047"),
              new AccessIdResource("Densee, Bo", "user048"),
              new AccessIdResource("Lerwagen, Bo", "user049"),
              new AccessIdResource("Tail, Bob", "user050"),
              new AccessIdResource("Ketta, Bruce", "user051"),
              new AccessIdResource("Terrie, Bud", "user052"),
              new AccessIdResource("Biener-Haken, Cara", "user053"),
              new AccessIdResource("Ass, Caro", "user054"),
              new AccessIdResource("Kaffee, Caro", "user055"),
              new AccessIdResource("Linger, Caro", "user056"),
              new AccessIdResource("tenSaft, Caro", "user057"),
              new AccessIdResource("Antheme, Chris", "user058"),
              new AccessIdResource("Baum, Chris", "user059"),
              new AccessIdResource("Tall, Chris", "user060"),
              new AccessIdResource("Reiniger, Claas", "user061"),
              new AccessIdResource("Grube, Claire", "user062"),
              new AccessIdResource("Fall, Clara", "user063"),
              new AccessIdResource("Korn, Clara", "user064"),
              new AccessIdResource("Lenriff, Cora", "user065"),
              new AccessIdResource("Schiert, Cora", "user066"),
              new AccessIdResource("Hose, Cord", "user067"),
              new AccessIdResource("Onbleu, Cord", "user068"),
              new AccessIdResource("Umkleide, Damon", "user069"),
              new AccessIdResource("Affier, Dean", "user070"),
              new AccessIdResource("Orm, Dean", "user071"),
              new AccessIdResource("Platz, Dennis", "user072"),
              new AccessIdResource("Milch, Dick", "user073"),
              new AccessIdResource("Mow, Dina", "user074"),
              new AccessIdResource("Keil, Donna", "user075"),
              new AccessIdResource("Littchen, Donna", "user076"),
              new AccessIdResource("Wetter, Donna", "user077"),
              new AccessIdResource("Was, Ed", "user078"),
              new AccessIdResource("Khar, Ede", "user079"),
              new AccessIdResource("Nut, Ella", "user080"),
              new AccessIdResource("Stisch, Ella", "user081"),
              new AccessIdResource("Diel, Emma", "user082"),
              new AccessIdResource("Herdamit, Emma", "user083"),
              new AccessIdResource("Mitter-Uhe, Emma", "user084"),
              new AccessIdResource("Tatt, Erich", "user085"),
              new AccessIdResource("Drigend, Ernie", "user086"),
              new AccessIdResource("Poly, Esther", "user087"),
              new AccessIdResource("Trautz, Eugen", "user088"),
              new AccessIdResource("Quiert, Eva", "user089"),
              new AccessIdResource("Inurlaub, Fatma", "user090"),
              new AccessIdResource("Land, Finn", "user091"),
              new AccessIdResource("Sternis, Finn", "user092"),
              new AccessIdResource("Furt, Frank", "user093"),
              new AccessIdResource("Reich, Frank", "user094"),
              new AccessIdResource("Iskaner, Franz", "user095"),
              new AccessIdResource("Nerr, Franziska", "user096"),
              new AccessIdResource("Zafen, Friedrich", "user097"),
              new AccessIdResource("Pomm, Fritz", "user098"),
              new AccessIdResource("deWegs, Gera", "user099"),
              new AccessIdResource("Staebe, Gitta", "user100"),
              new AccessIdResource("Zend, Glenn", "user101"),
              new AccessIdResource("Fisch, Grete", "user102"),
              new AccessIdResource("Zucker, Gus", "user103"),
              new AccessIdResource("Muhn, Hanni", "user104"),
              new AccessIdResource("Fermesse, Hanno", "user105"),
              new AccessIdResource("Aplast, Hans", "user106"),
              new AccessIdResource("Eart, Hans", "user107"),
              new AccessIdResource("Back, Hardy", "user108"),
              new AccessIdResource("Beau, Harry", "user109"),
              new AccessIdResource("Kraut, Heide", "user110"),
              new AccessIdResource("Witzka, Heide", "user111"),
              new AccessIdResource("Buchen, Hein", "user112"),
              new AccessIdResource("Lichkeit, Hein", "user113"),
              new AccessIdResource("Suchung, Hein", "user114"),
              new AccessIdResource("Ellmann, Heinz", "user115"),
              new AccessIdResource("Ketchup, Heinz", "user116"),
              new AccessIdResource("Zeim, Hilde", "user117"),
              new AccessIdResource("Bilien, Immo", "user118"),
              new AccessIdResource("Her, Inge", "user119"),
              new AccessIdResource("Wahrsam, Inge", "user120"),
              new AccessIdResource("Flamm, Ingo", "user121"),
              new AccessIdResource("Enzien, Ingrid", "user122"),
              new AccessIdResource("Rohsch, Inken", "user123"),
              new AccessIdResource("Ihr, Insa", "user124"),
              new AccessIdResource("Nerda, Iska", "user125"),
              new AccessIdResource("Eitz, Jens", "user126"),
              new AccessIdResource("Nastik, Jim", "user127"),
              new AccessIdResource("Gurt, Jo", "user128"),
              new AccessIdResource("Kurrth, Jo", "user129"),
              new AccessIdResource("Kolade, Joe", "user130"),
              new AccessIdResource("Iter, Johann", "user131"),
              new AccessIdResource("Tick, Joyce", "user132"),
              new AccessIdResource("Case, Justin", "user133"),
              new AccessIdResource("Time, Justin", "user134"),
              new AccessIdResource("Komp, Jutta", "user135"),
              new AccessIdResource("Mauer, Kai", "user136"),
              new AccessIdResource("Pirinja, Kai", "user137"),
              new AccessIdResource("Serpfalz, Kai", "user138"),
              new AccessIdResource("Auer, Karl", "user139"),
              new AccessIdResource("Ielauge, Karl", "user140"),
              new AccessIdResource("Ifornjen, Karl", "user141"),
              new AccessIdResource("Radi, Karl", "user142"),
              new AccessIdResource("Verti, Karl", "user143"),
              new AccessIdResource("Sery, Karo", "user144"),
              new AccessIdResource("Lisator, Katha", "user145"),
              new AccessIdResource("Flo, Kati", "user146"),
              new AccessIdResource("Schenn, Knut", "user147"),
              new AccessIdResource("Achse, Kurt", "user148"),
              new AccessIdResource("Zepause, Kurt", "user149"),
              new AccessIdResource("Zerr, Kurt", "user150"),
              new AccessIdResource("Reden, Lasse", "user151"),
              new AccessIdResource("Metten, Lee", "user152"),
              new AccessIdResource("Arm, Lene", "user153"),
              new AccessIdResource("Thur, Linnea", "user154"),
              new AccessIdResource("Bonn, Lisa", "user155"),
              new AccessIdResource("Sembourg, Luc", "user156"),
              new AccessIdResource("Rung, Lucky", "user157"),
              new AccessIdResource("Zafen, Ludwig", "user158"),
              new AccessIdResource("Hauden, Lukas", "user159"),
              new AccessIdResource("Hose, Lutz", "user160"),
              new AccessIdResource("Tablette, Lutz", "user161"),
              new AccessIdResource("Fehr, Luzie", "user162"),
              new AccessIdResource("Nalyse, Magda", "user163"),
              new AccessIdResource("Ehfer, Maik", "user164"),
              new AccessIdResource("Sehr, Malte", "user165"),
              new AccessIdResource("Thon, Mara", "user166"),
              new AccessIdResource("Quark, Marga", "user167"),
              new AccessIdResource("Nade, Marie", "user168"),
              new AccessIdResource("Niert, Marie", "user169"),
              new AccessIdResource("Neese, Mario", "user170"),
              new AccessIdResource("Nette, Marion", "user171"),
              new AccessIdResource("Nesium, Mark", "user172"),
              new AccessIdResource("Thalle, Mark", "user173"),
              new AccessIdResource("Diven, Marle", "user174"),
              new AccessIdResource("Fitz, Marle", "user175"),
              new AccessIdResource("Pfahl, Marta", "user176"),
              new AccessIdResource("Zorn, Martin", "user177"),
              new AccessIdResource("Krissmes, Mary", "user178"),
              new AccessIdResource("Jess, Matt", "user179"),
              new AccessIdResource("Strammer, Max", "user180"),
              new AccessIdResource("Mumm, Maxi", "user181"),
              new AccessIdResource("Morphose, Meta", "user182"),
              new AccessIdResource("Uh, Mia", "user183"),
              new AccessIdResource("Rofon, Mike", "user184"),
              new AccessIdResource("Rosoft, Mike", "user185"),
              new AccessIdResource("Liter, Milli", "user186"),
              new AccessIdResource("Thär, Milli", "user187"),
              new AccessIdResource("Welle, Mirko", "user188"),
              new AccessIdResource("Thorat, Mo", "user189"),
              new AccessIdResource("Thor, Moni", "user190"),
              new AccessIdResource("Kinolta, Monika", "user191"),
              new AccessIdResource("Mundhaar, Monika", "user192"),
              new AccessIdResource("Munter, Monika", "user193"),
              new AccessIdResource("Zwerg, Nat", "user194"),
              new AccessIdResource("Elmine, Nick", "user195"),
              new AccessIdResource("Thien, Niko", "user196"),
              new AccessIdResource("Pferd, Nils", "user197"),
              new AccessIdResource("Lerweise, Norma", "user198"),
              new AccessIdResource("Motor, Otto", "user199"),
              new AccessIdResource("Totol, Otto", "user200"),
              new AccessIdResource("Nerr, Paula", "user201"),
              new AccessIdResource("Imeter, Peer", "user202"),
              new AccessIdResource("Serkatze, Peer", "user203"),
              new AccessIdResource("Gogisch, Peter", "user204"),
              new AccessIdResource("Silje, Peter", "user205"),
              new AccessIdResource("Harmonie, Phil", "user206"),
              new AccessIdResource("Ihnen, Philip", "user207"),
              new AccessIdResource("Uto, Pia", "user208"),
              new AccessIdResource("Kothek, Pina", "user209"),
              new AccessIdResource("Zar, Pit", "user210"),
              new AccessIdResource("Zeih, Polly", "user211"),
              new AccessIdResource("Tswan, Puh", "user212"),
              new AccessIdResource("Zufall, Rainer", "user213"),
              new AccessIdResource("Lien, Rita", "user214"),
              new AccessIdResource("Held, Roman", "user215"),
              new AccessIdResource("Haar, Ross", "user216"),
              new AccessIdResource("Dick, Roy", "user217"),
              new AccessIdResource("Enplaner, Ruth", "user218"),
              new AccessIdResource("Kommen, Ryan", "user219"),
              new AccessIdResource("Philo, Sophie", "user220"),
              new AccessIdResource("Matisier, Stig", "user221"),
              new AccessIdResource("Loniki, Tessa", "user222"),
              new AccessIdResource("Tralisch, Thea", "user223"),
              new AccessIdResource("Logie, Theo", "user224"),
              new AccessIdResource("Ister, Thorn", "user225"),
              new AccessIdResource("Buktu, Tim", "user226"),
              new AccessIdResource("Ate, Tom", "user227"),
              new AccessIdResource("Pie, Udo", "user228"),
              new AccessIdResource("Aloe, Vera", "user229"),
              new AccessIdResource("Hausver, Walter", "user230"),
              new AccessIdResource("Schuh, Wanda", "user231"),
              new AccessIdResource("Rahm, Wolf", "user232"),
              new AccessIdResource("businessadmin", "cn=businessadmin,ou=groups,o=taskanatest"),
              new AccessIdResource("UsersGroup", "cn=usersgroup,ou=groups,o=taskanatest"),
              new AccessIdResource("DevelopersGroup", "cn=developersgroup,ou=groups,o=taskanatest"),
              new AccessIdResource("businessadmin", "cn=customersgroup,ou=groups,o=taskanatest"),
              new AccessIdResource("user_domain_A", "cn=user_domain_a,ou=groups,o=taskanatest"),
              new AccessIdResource("monitor", "cn=monitor,ou=groups,o=taskanatest"),
              new AccessIdResource("user_domain_C", "cn=user_domain_c,ou=groups,o=taskanatest"),
              new AccessIdResource("user_domain_D", "cn=user_domain_d,ou=groups,o=taskanatest"),
              new AccessIdResource("admin", "cn=admin,ou=groups,o=taskanatest"),
              new AccessIdResource(
                  "manager_domain_B", "cn=manager_domain_b,ou=groups,o=taskanatest"),
              new AccessIdResource(
                  "manager_domain_C", "cn=manager_domain_c,ou=groups,o=taskanatest"),
              new AccessIdResource(
                  "manager_domain_D", "cn=manager_domain_d,ou=groups,o=taskanatest"),
              new AccessIdResource("teamlead_2", "cn=teamlead_2" + ",ou=groups,o=taskanatest"),
              new AccessIdResource("teamlead_4", "cn=teamlead_4" + ",ou=groups,o=taskanatest"),
              new AccessIdResource("team_3", "cn=team_3" + ",ou=groups,o=taskanatest"),
              new AccessIdResource("team_4", "cn=team_4" + ",ou=groups,o=taskanatest")));

  @Override
  public List<AccessIdResource> findMatchingAccessId(
      String searchFor, int maxNumberOfReturnedAccessIds) {
    return findAcessIdResource(searchFor, maxNumberOfReturnedAccessIds, false);
  }

  @Override
  public List<AccessIdResource> findGroupsOfUser(
      String searchFor, int maxNumberOfReturnedAccessIds) {
    if (users == null) {
      addUsersToGroups();
    }
    return findAcessIdResource(searchFor, maxNumberOfReturnedAccessIds, true);
  }

  @Override
  public List<AccessIdResource> validateAccessId(String accessId) {
    return accessIds.stream()
        .filter(t -> (t.getAccessId().equalsIgnoreCase(accessId.toLowerCase())))
        .collect(Collectors.toList());
  }

  private List<AccessIdResource> findAcessIdResource(
      String searchFor, int maxNumberOfReturnedAccessIds, boolean groupMember) {
    List<AccessIdResource> usersAndGroups =
        accessIds.stream()
            .filter(
                t ->
                    (t.getName().toLowerCase().contains(searchFor.toLowerCase())
                        || t.getAccessId().toLowerCase().contains(searchFor.toLowerCase())))
            .collect(Collectors.toList());

    List<AccessIdResource> usersAndGroupsAux = new ArrayList<>(usersAndGroups);
    if (groupMember) {
      usersAndGroupsAux.forEach(
          item -> {
            if (users.get(item) != null) {
              usersAndGroups.addAll(users.get(item));
            }
          });
    }

    usersAndGroups.sort(
        (AccessIdResource a, AccessIdResource b) -> {
          return a.getAccessId().compareToIgnoreCase(b.getAccessId());
        });

    List<AccessIdResource> result =
        usersAndGroups.subList(0, Math.min(usersAndGroups.size(), maxNumberOfReturnedAccessIds));

    return result;
  }

  private void addUsersToGroups() {
    List<AccessIdResource> groups = new ArrayList<>();
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
    List<AccessIdResource> group0 = new ArrayList<>();
    List<AccessIdResource> group1 = new ArrayList<>();
    List<AccessIdResource> group2 = new ArrayList<>();
    List<AccessIdResource> group3 = new ArrayList<>();

    for (AccessIdResource group : groups) {
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
    for (AccessIdResource item : accessIds) {
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
