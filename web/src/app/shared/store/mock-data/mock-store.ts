import { Workbasket } from '../../models/workbasket';
import { WorkbasketType } from '../../models/workbasket-type';
import { ACTION } from '../../models/action';
import { WorkbasketAccessItemsRepresentation } from '../../models/workbasket-access-items-representation';

export const classificationStateMock = {
  classifications: [],
  selectedClassification: { classificationId: 'ID01', key: 'KEY01', name: 'Recommendation' },
  selectedClassificationType: 'DOCUMENT',
  classificationTypes: {
    TASK: [],
    DOCUMENT: ['EXTERNAL', 'MANUAL', 'AUTOMATIC']
  },
  badgeMessage: 'Creating new classification'
};

export const engineConfigurationMock = {
  customisation: {
    EN: {
      global: {
        debounceTimeLookupField: 50
      },
      workbaskets: {
        information: {
          owner: {
            lookupField: true
          },
          custom1: {
            field: 'Customized field 1 title',
            visible: true
          },
          custom3: {
            field: '',
            visible: false
          }
        },
        'access-items': {
          accessId: {
            lookupField: true
          },
          custom3: {
            field: '',
            visible: false
          },
          custom9: {
            field: 'Some custom field',
            visible: true
          },
          custom10: {
            field: '',
            visible: false
          },
          custom11: {
            field: '',
            visible: false
          },
          custom12: {
            field: '',
            visible: false
          }
        }
      },
      classifications: {
        information: {
          custom1: {
            field: 'Classification custom 1',
            visible: true
          },
          custom3: {
            field: '',
            visible: false
          }
        },
        categories: {
          EXTERNAL: 'assets/icons/categories/external.svg',
          MANUAL: 'assets/icons/categories/manual.svg',
          AUTOMATIC: 'assets/icons/categories/automatic.svg',
          PROCESS: 'assets/icons/categories/process.svg',
          missing: 'assets/icons/categories/missing-icon.svg'
        }
      },
      tasks: {
        information: {
          owner: {
            lookupField: true
          }
        }
      }
    }
  },
  language: 'EN'
};

export const selectedWorkbasketMock: Workbasket = {
  workbasketId: 'WBI:000000000000000000000000000000000902',
  key: 'sOrt003',
  name: 'bAsxet2',
  domain: 'DOMAIN_A',
  type: WorkbasketType.TOPIC,
  description: 'Lorem ipsum dolor sit amet.',
  owner: 'Max',
  custom1: '',
  custom2: '',
  custom3: '',
  custom4: '',
  orgLevel1: '',
  orgLevel2: '',
  orgLevel3: '',
  orgLevel4: '',
  markedForDeletion: false,
  created: '2020-08-18T09:14:41.353Z',
  modified: '2020-08-18T09:14:41.353Z',
  _links: {
    self: {
      href: 'http://localhost:8080/kadai/api/v1/workbaskets/WBI:000000000000000000000000000000000902'
    },
    distributionTargets: {
      href: 'http://localhost:8080/kadai/api/v1/workbaskets/WBI:000000000000000000000000000000000902/distribution-targets'
    },
    accessItems: {
      href: 'http://localhost:8080/kadai/api/v1/workbaskets/WBI:000000000000000000000000000000000902/workbasketAccessItems'
    },
    allWorkbaskets: {
      href: 'http://localhost:8080/kadai/api/v1/workbaskets'
    },
    removeDistributionTargets: {
      href: 'http://localhost:8080/kadai/api/v1/workbaskets/WBI:000000000000000000000000000000000902/distribution-targets'
    }
  }
};

export const workbasketAccessItemsMock: WorkbasketAccessItemsRepresentation = {
  accessItems: [
    {
      accessItemId: 'WBI:000000000000000000000000000000000901',
      workbasketId: 'WBI:000000000000000000000000000000000901',
      workbasketKey: 'Sort002',
      accessId: 'user-b-1',
      accessName: 'Bern, Bernd',
      permRead: true,
      permOpen: true,
      permAppend: true,
      permTransfer: true,
      permDistribute: true,
      permReadTasks: true,
      permEditTasks: true,
      permCustom1: true,
      permCustom2: true,
      permCustom3: true,
      permCustom4: true,
      permCustom5: true,
      permCustom6: true,
      permCustom7: true,
      permCustom8: true,
      permCustom9: true,
      permCustom10: true,
      permCustom11: true,
      permCustom12: true
    },
    {
      accessItemId: 'WBI:000000000000000000000000000000000901',
      workbasketId: 'WBI:000000000000000000000000000000000901',
      workbasketKey: 'Sort002',
      accessId: 'user-b-0',
      accessName: 'Braun, Oliver',
      permRead: true,
      permOpen: true,
      permAppend: true,
      permTransfer: true,
      permDistribute: false,
      permReadTasks: true,
      permEditTasks: true,
      permCustom1: true,
      permCustom2: true,
      permCustom3: true,
      permCustom4: true,
      permCustom5: true,
      permCustom6: true,
      permCustom7: true,
      permCustom8: true,
      permCustom9: true,
      permCustom10: true,
      permCustom11: true,
      permCustom12: true
    }
  ],
  _links: {
    self: {
      href: 'http://localhost:8080/kadai/api/v1/workbaskets/WBI:000000000000000000000000000000000901/workbasketAccessItems'
    },
    workbasket: {
      href: 'http://localhost:8080/kadai/api/v1/workbaskets/WBI:000000000000000000000000000000000901'
    }
  }
};

export const workbasketAvailableDistributionTargets = {
  workbaskets: [
    {
      workbasketId: 'WBI:000000000000000000000000000000000000',
      key: 'ADMIN',
      name: 'Postkorb Admin',
      domain: 'DOMAIN_A',
      type: 'PERSONAL',
      description: 'Postkorb Admin',
      owner: '',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:000000000000000000000000000000000900',
      key: 'sort001',
      name: 'basxet0',
      domain: 'DOMAIN_A',
      type: 'TOPIC',
      description: 'Lorem ipsum dolor sit amet.',
      owner: 'user-1-3',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:000000000000000000000000000000000901',
      key: 'Sort002',
      name: 'Basxet1',
      domain: 'DOMAIN_A',
      type: 'TOPIC',
      description: 'Lorem ipsum dolor sit amet.',
      owner: 'user-1-3',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:000000000000000000000000000000000902',
      key: 'sOrt003',
      name: 'bAsxet2',
      domain: 'DOMAIN_A',
      type: 'TOPIC',
      description: 'Lorem ipsum dolor sit amet.',
      owner: 'user-1-3',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:000000000000000000000000000000000903',
      key: 'soRt004',
      name: 'baSxet3',
      domain: 'DOMAIN_A',
      type: 'TOPIC',
      description: 'Lorem ipsum dolor sit amet.',
      owner: 'user-1-3',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:000000000000000000000000000000000904',
      key: 'sorT005',
      name: 'basXet4',
      domain: 'DOMAIN_A',
      type: 'TOPIC',
      description: 'Lorem ipsum dolor sit amet.',
      owner: 'user-1-3',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:000000000000000000000000000000000905',
      key: 'Sort006',
      name: 'basxEt5',
      domain: 'DOMAIN_A',
      type: 'TOPIC',
      description: 'Lorem ipsum dolor sit amet.',
      owner: 'user-1-3',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:000000000000000000000000000000000906',
      key: 'SOrt007',
      name: 'basxeT6',
      domain: 'DOMAIN_A',
      type: 'TOPIC',
      description: 'Lorem ipsum dolor sit amet.',
      owner: 'user-1-3',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:000000000000000000000000000000000907',
      key: 'SoRt008',
      name: 'BAsxet7',
      domain: 'DOMAIN_A',
      type: 'TOPIC',
      description: 'Lorem ipsum dolor sit amet.',
      owner: 'user-1-3',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:000000000000000000000000000000000908',
      key: 'SorT009',
      name: 'BaSxet8',
      domain: 'DOMAIN_A',
      type: 'TOPIC',
      description: 'Lorem ipsum dolor sit amet.',
      owner: 'user-1-3',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:000000000000000000000000000000000909',
      key: 'Sort010',
      name: 'BasXet9',
      domain: 'DOMAIN_A',
      type: 'TOPIC',
      description: 'Lorem ipsum dolor sit amet.',
      owner: 'user-1-3',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:100000000000000000000000000000000004',
      key: 'TEAMLEAD-1',
      name: 'PPK Teamlead KSC 1',
      domain: 'DOMAIN_A',
      type: 'PERSONAL',
      description: 'PPK Teamlead KSC 1',
      owner: '',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:100000000000000000000000000000000005',
      key: 'TEAMLEAD-2',
      name: 'PPK Teamlead KSC 2',
      domain: 'DOMAIN_A',
      type: 'PERSONAL',
      description: 'PPK Teamlead KSC 2',
      owner: '',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:100000000000000000000000000000000006',
      key: 'USER-1-1',
      name: 'PPK User 1 KSC 1',
      domain: 'DOMAIN_A',
      type: 'PERSONAL',
      description: 'PPK User 1 KSC 1',
      owner: '',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: 'custom4z',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:100000000000000000000000000000000007',
      key: 'USER-1-2',
      name: 'PPK User 2 KSC 1',
      domain: 'DOMAIN_A',
      type: 'PERSONAL',
      description: 'PPK User 2 KSC 1',
      owner: 'user-1-2',
      custom1: 'custom1',
      custom2: 'custom2',
      custom3: 'custom3',
      custom4: 'custom4',
      orgLevel1: 'versicherung',
      orgLevel2: 'abteilung',
      orgLevel3: 'projekt',
      orgLevel4: 'team',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:100000000000000000000000000000000008',
      key: 'USER-2-1',
      name: 'PPK User 1 KSC 2',
      domain: 'DOMAIN_A',
      type: 'PERSONAL',
      description: 'PPK User 1 KSC 2',
      owner: '',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:100000000000000000000000000000000009',
      key: 'USER-2-2',
      name: 'PPK User 2 KSC 2',
      domain: 'DOMAIN_A',
      type: 'PERSONAL',
      description: 'PPK User 2 KSC 2',
      owner: '',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:100000000000000000000000000000000010',
      key: 'TPK_VIP',
      name: 'Themenpostkorb VIP',
      domain: 'DOMAIN_A',
      type: 'TOPIC',
      description: 'Themenpostkorb VIP',
      owner: '',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:100000000000000000000000000000000011',
      key: 'GPK_B_KSC',
      name: 'Gruppenpostkorb KSC B',
      domain: 'DOMAIN_B',
      type: 'GROUP',
      description: 'Gruppenpostkorb KSC',
      owner: '',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:100000000000000000000000000000000012',
      key: 'GPK_B_KSC_1',
      name: 'Gruppenpostkorb KSC B1',
      domain: 'DOMAIN_B',
      type: 'GROUP',
      description: 'Gruppenpostkorb KSC 1',
      owner: '',
      custom1: 'custom1',
      custom2: 'custom2',
      custom3: 'custom3',
      custom4: 'custom4',
      orgLevel1: 'orgl1',
      orgLevel2: 'orgl2',
      orgLevel3: 'orgl3',
      orgLevel4: 'aorgl4',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:100000000000000000000000000000000013',
      key: 'GPK_B_KSC_2',
      name: 'Gruppenpostkorb KSC B2',
      domain: 'DOMAIN_B',
      type: 'GROUP',
      description: 'Gruppenpostkorb KSC 2',
      owner: '',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:100000000000000000000000000000000014',
      key: 'USER-B-1',
      name: 'PPK User 1 KSC 1 Domain B',
      domain: 'DOMAIN_B',
      type: 'PERSONAL',
      description: 'PPK User 1 KSC 1 Domain B',
      owner: '',
      custom1: '',
      custom2: 'custom20',
      custom3: '',
      custom4: 'custom4',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:100000000000000000000000000000000015',
      key: 'USER-B-2',
      name: 'PPK User 2 KSC 1 Domain B',
      domain: 'DOMAIN_B',
      type: 'PERSONAL',
      description: 'PPK User 1 KSC 1 Domain B',
      owner: 'user-1-2',
      custom1: 'ABCABC',
      custom2: 'cust2',
      custom3: 'cust3',
      custom4: 'cust4',
      orgLevel1: 'orgl1',
      orgLevel2: 'orgl2',
      orgLevel3: 'orgl3',
      orgLevel4: 'orgl4',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:100000000000000000000000000000000016',
      key: 'TPK_VIP_2',
      name: 'Themenpostkorb VIP 2',
      domain: 'DOMAIN_A',
      type: 'TOPIC',
      description: 'Themenpostkorb VIP',
      owner: '',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:100000000000000000000000000000000017',
      key: 'das_ist_ein_sehr_sehr_sehr_sehr_sehr_sehr_sehr_sehr_langer_key_1',
      name: 'Testpostkorb',
      domain: 'DOMAIN_TEST',
      type: 'TOPIC',
      description: null,
      owner: '',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:100000000000000000000000000000000018',
      key: 'das_ist_eine_lange_description_und_ein_langer_owner',
      name: 'Testpostkorb',
      domain: 'DOMAIN_TEST',
      type: 'TOPIC',
      description:
        'Lorem ipsum dolor sit amet, consetetur sadipscingsed diam nonumy eirmod tempor invidunt ut labore sed diam nonumy eirmod tempor invidunt ut labore ore magna aliquyam erat, sed diam voluptua. At ves et accusam et justo duo dolores abcdfiskdk ekeke',
      owner: 'das_ist_eine_sehr_sehr_sehr_sehr_sehr_lange_user_id',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:100000000000000000000000000000000019',
      key: 'das_ist_ein_sehr_sehr_sehr_sehr_sehr_sehr_sehr_sehr_langer_key_2',
      name: 'das_ist_ein_sehr_sehr_sehr_sehr_sehr_sehr_sehr_sehr_sehr_sehr_langer_Testpostkorbname_ohne_Leerzeichen',
      domain: 'DOMAIN_TEST',
      type: 'TOPIC',
      description: 'langer Key und langer Name ohne Leerzeichen',
      owner: '',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:100000000000000000000000000000000020',
      key: 'das_ist_ein_sehr_sehr_sehr_sehr_sehr_sehr_sehr_sehr_langer_key_3',
      name: 'das ist ein sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr langer Testpostkorbname mit Leerzeichen 1',
      domain: 'DOMAIN_TEST',
      type: 'TOPIC',
      description: 'langer Key und langer Name mit Leerzeichen',
      owner: '',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:100000000000000000000000000000000021',
      key: 'das_ist_ein_sehr_sehr_sehr_sehr_sehr_sehr_sehr_sehr_langer_key_4',
      name: 'das ist ein sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr langer Testpostkorbname mit Leerzeichen 2',
      domain: 'DOMAIN_TEST',
      type: 'TOPIC',
      description: 'langer Key, langer Name mit Leerzeichen und lange UserId',
      owner: 'das_ist_eine_sehr_sehr_sehr_sehr_sehr_lange_user_id',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:100000000000000000000000000000000022',
      key: 'kurzer_key',
      name: 'das ist ein sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr langer Testpostkorbname mit Leerzeichen 3',
      domain: 'DOMAIN_TEST',
      type: 'TOPIC',
      description: 'kurzer Key und langer Name mit Leerzeichen',
      owner: '',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:100000000000000000000000000000000023',
      key: 'langer key, langer name, eine lange description, langer owner',
      name: 'das ist ein sehr sehr sehr sehr sehr sehr sehr sehr sehr sehr langer Testpostkorbname mit Leerzeichen 4',
      domain: 'DOMAIN_TEST',
      type: 'TOPIC',
      description:
        'Lorem ipsum dolor sit amet, consetetur sadipscingsed diam nonumy eirmod tempor invidunt ut labore sed diam nonumy eirmod tempor invidunt ut labore ore magna aliquyam erat, sed diam voluptua. At ves et accusam et justo duo dolores abcdfiskdk ekeke',
      owner: 'das_ist_eine_sehr_sehr_sehr_sehr_sehr_lange_user_id',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    }
  ],
  page: {
    size: 40,
    totalElements: 34,
    totalPages: 1,
    number: 1
  },
  _links: {
    self: {
      href: 'http://localhost:8080/kadai/api/v1/workbaskets/?page=1&page-size=40'
    },
    first: {
      href: 'http://localhost:8080/kadai/api/v1/workbaskets/?page-size=40&page=1'
    },
    last: {
      href: 'http://localhost:8080/kadai/api/v1/workbaskets/?page-size=40&page=1'
    }
  }
};

export const workbasketReadStateMock = {
  selectedWorkbasket: selectedWorkbasketMock,
  paginatedWorkbasketsSummary: {
    _links: {
      self: {
        href: 'http://localhost:8080/kadai/api/v1/workbaskets/?sort-by=name&order=asc&domain=DOMAIN_A&page=3&page-size=8'
      },
      first: {
        href: 'http://localhost:8080/kadai/api/v1/workbaskets/?sort-by=name&order=asc&domain=DOMAIN_A&page-size=8&page=1'
      },
      last: {
        href: 'http://localhost:8080/kadai/api/v1/workbaskets/?sort-by=name&order=asc&domain=DOMAIN_A&page-size=8&page=3'
      },
      prev: {
        href: 'http://localhost:8080/kadai/api/v1/workbaskets/?sort-by=name&order=asc&domain=DOMAIN_A&page-size=8&page=2'
      }
    },
    workbaskets: [
      {
        workbasketId: 'WBI:100000000000000000000000000000000008',
        key: 'USER-2-1',
        name: 'PPK User 1 KSC 2',
        domain: 'DOMAIN_A',
        type: WorkbasketType.PERSONAL,
        description: 'PPK User 1 KSC 2',
        owner: '',
        custom1: '',
        custom2: '',
        custom3: '',
        custom4: '',
        orgLevel1: '',
        orgLevel2: '',
        orgLevel3: '',
        orgLevel4: '',
        markedForDeletion: false
      },
      {
        workbasketId: 'WBI:100000000000000000000000000000000007',
        key: 'USER-1-2',
        name: 'PPK User 2 KSC 1',
        domain: 'DOMAIN_A',
        type: WorkbasketType.PERSONAL,
        description: 'PPK User 2 KSC 1',
        owner: 'Peter Maier',
        custom1: 'custom1',
        custom2: 'custom2',
        custom3: 'custom3',
        custom4: 'custom4',
        orgLevel1: 'versicherung',
        orgLevel2: 'abteilung',
        orgLevel3: 'projekt',
        orgLevel4: 'team',
        markedForDeletion: false
      },
      {
        workbasketId: 'WBI:100000000000000000000000000000000009',
        key: 'USER-2-2',
        name: 'PPK User 2 KSC 2',
        domain: 'DOMAIN_A',
        type: WorkbasketType.PERSONAL,
        description: 'PPK User 2 KSC 2',
        owner: '',
        custom1: '',
        custom2: '',
        custom3: '',
        custom4: '',
        orgLevel1: '',
        orgLevel2: '',
        orgLevel3: '',
        orgLevel4: '',
        markedForDeletion: false
      },
      {
        workbasketId: 'WBI:100000000000000000000000000000000010',
        key: 'TPK_VIP',
        name: 'Themenpostkorb VIP',
        domain: 'DOMAIN_A',
        type: WorkbasketType.TOPIC,
        description: 'Themenpostkorb VIP',
        owner: '',
        custom1: '',
        custom2: '',
        custom3: '',
        custom4: '',
        orgLevel1: '',
        orgLevel2: '',
        orgLevel3: '',
        orgLevel4: '',
        markedForDeletion: false
      },
      {
        workbasketId: 'WBI:100000000000000000000000000000000016',
        key: 'TPK_VIP_2',
        name: 'Themenpostkorb VIP 2',
        domain: 'DOMAIN_A',
        type: WorkbasketType.TOPIC,
        description: 'Themenpostkorb VIP',
        owner: '',
        custom1: '',
        custom2: '',
        custom3: '',
        custom4: '',
        orgLevel1: '',
        orgLevel2: '',
        orgLevel3: '',
        orgLevel4: '',
        markedForDeletion: false
      }
    ],
    page: {
      size: 8,
      totalElements: 21,
      totalPages: 3,
      number: 3
    }
  },
  action: ACTION.READ,
  workbasketAvailableDistributionTargets: [
    {
      workbasketId: 'WBI:100000000000000000000000000000000001',
      key: 'GPK_KSC',
      name: 'Gruppenpostkorb KSC',
      domain: 'DOMAIN_A',
      type: 'GROUP',
      description: 'Gruppenpostkorb KSC',
      owner: 'owner0815',
      custom1: 'ABCQVW',
      custom2: '',
      custom3: 'xyz4',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:100000000000000000000000000000000002',
      key: 'GPK_KSC_1',
      name: 'Gruppenpostkorb KSC 1',
      domain: 'DOMAIN_A',
      type: 'GROUP',
      description: 'Gruppenpostkorb KSC 1',
      owner: '',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:100000000000000000000000000000000003',
      key: 'GPK_KSC_2',
      name: 'Gruppenpostkorb KSC 2',
      domain: 'DOMAIN_A',
      type: 'GROUP',
      description: 'Gruppenpostkorb KSC 2',
      owner: '',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:000000000000000000000000000000000900',
      key: 'sort001',
      name: 'basxet0',
      domain: 'DOMAIN_A',
      type: 'TOPIC',
      description: 'Lorem ipsum dolor sit amet.',
      owner: 'Max',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:000000000000000000000000000000000901',
      key: 'Sort002',
      name: 'Basxet1',
      domain: 'DOMAIN_A',
      type: 'TOPIC',
      description: 'Lorem ipsum dolor sit amet.',
      owner: 'Max',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:000000000000000000000000000000000902',
      key: 'sOrt003',
      name: 'bAsxet2',
      domain: 'DOMAIN_A',
      type: 'TOPIC',
      description: 'Lorem ipsum dolor sit amet.',
      owner: 'Max',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:000000000000000000000000000000000903',
      key: 'soRt004',
      name: 'baSxet3',
      domain: 'DOMAIN_A',
      type: 'TOPIC',
      description: 'Lorem ipsum dolor sit amet.',
      owner: 'Max',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    },
    {
      workbasketId: 'WBI:000000000000000000000000000000000904',
      key: 'sorT005',
      name: 'basXet4',
      domain: 'DOMAIN_A',
      type: 'TOPIC',
      description: 'Lorem ipsum dolor sit amet.',
      owner: 'Max',
      custom1: '',
      custom2: '',
      custom3: '',
      custom4: '',
      orgLevel1: '',
      orgLevel2: '',
      orgLevel3: '',
      orgLevel4: '',
      markedForDeletion: false
    }
  ],
  workbasketAccessItems: workbasketAccessItemsMock,
  workbasketDistributionTargets: {
    _links: {
      self: {
        href: 'http://localhost:8080/kadai/api/v1/workbaskets/WBI:000000000000000000000000000000000900/distribution-targets'
      }
    },
    distributionTargets: [
      {
        workbasketId: 'WBI:100000000000000000000000000000000001',
        key: 'GPK_KSC',
        name: 'Gruppenpostkorb KSC',
        domain: 'DOMAIN_A',
        type: 'GROUP',
        description: 'Gruppenpostkorb KSC',
        owner: 'owner0815',
        custom1: 'ABCQVW',
        custom2: '',
        custom3: 'xyz4',
        custom4: '',
        orgLevel1: '',
        orgLevel2: '',
        orgLevel3: '',
        orgLevel4: '',
        markedForDeletion: false
      },
      {
        workbasketId: 'WBI:100000000000000000000000000000000002',
        key: 'GPK_KSC_1',
        name: 'Gruppenpostkorb KSC 1',
        domain: 'DOMAIN_A',
        type: 'GROUP',
        description: 'Gruppenpostkorb KSC 1',
        owner: '',
        custom1: '',
        custom2: '',
        custom3: '',
        custom4: '',
        orgLevel1: '',
        orgLevel2: '',
        orgLevel3: '',
        orgLevel4: '',
        markedForDeletion: false
      },
      {
        workbasketId: 'WBI:100000000000000000000000000000000003',
        key: 'GPK_KSC_2',
        name: 'Gruppenpostkorb KSC 2',
        domain: 'DOMAIN_A',
        type: 'GROUP',
        description: 'Gruppenpostkorb KSC 2',
        owner: '',
        custom1: '',
        custom2: '',
        custom3: '',
        custom4: '',
        orgLevel1: '',
        orgLevel2: '',
        orgLevel3: '',
        orgLevel4: '',
        markedForDeletion: false
      }
    ]
  },
  distributionTargetsPage: 0,
  availableDistributionTargets: workbasketAvailableDistributionTargets,
  availableDistributionTargetsPage: 0
};

export const settingsStateMock = {
  settings: {
    nameHighPriority: 'High Priority',
    nameMediumPriority: 'Medium Priority',
    nameLowPriority: 'Low Priority',
    intervalHighPriority: [3, 300],
    intervalMediumPriority: [2, 2],
    intervalLowPriority: [0, 1],
    colorHighPriority: '#FF0000',
    colorLowPriority: '#5FAD00',
    colorMediumPriority: '#FFD700',
    filter: '{ "Tasks with state READY": { "state": ["READY"]}, "Tasks with state CLAIMED": {"state": ["CLAIMED"] }}',
    schema: [
      {
        displayName: 'Priority Report',
        members: [
          {
            key: 'nameHighPriority',
            displayName: 'High Priority Name',
            type: 'text',
            max: 32
          },
          {
            key: 'nameMediumPriority',
            displayName: 'Medium Priority Name',
            type: 'text',
            min: 0,
            max: 32
          },
          {
            key: 'nameLowPriority',
            displayName: 'Low Priority Name',
            type: 'text',
            min: 0,
            max: 32
          },
          {
            key: 'intervalHighPriority',
            displayName: 'High Priority Interval',
            type: 'interval',
            min: 0
          },
          {
            key: 'intervalMediumPriority',
            displayName: 'Medium Priority Interval',
            type: 'interval',
            min: 0
          },
          {
            key: 'intervalLowPriority',
            displayName: 'Low Priority Interval',
            type: 'interval',
            min: 0
          },
          {
            key: 'colorHighPriority',
            displayName: 'High Priority Color',
            type: 'color'
          },
          {
            key: 'colorMediumPriority',
            displayName: 'Medium Priority Color',
            type: 'color'
          },
          {
            key: 'colorLowPriority',
            displayName: 'Low Priority Color',
            type: 'color'
          }
        ]
      },
      {
        displayName: 'Filter for Task-Priority-Report',
        members: [
          {
            key: 'filter',
            displayName: 'Filter values',
            type: 'json',
            min: 1
          }
        ]
      }
    ]
  }
};
