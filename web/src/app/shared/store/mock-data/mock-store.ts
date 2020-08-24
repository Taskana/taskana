import { Workbasket } from '../../models/workbasket';
import { ICONTYPES } from '../../models/icon-types';
import { ACTION } from '../../models/action';

export const engineConfigurationMock = {
  customisation: {
    EN: {
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
  type: ICONTYPES.TOPIC,
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
      href: 'http://localhost:8080/taskana/api/v1/workbaskets/WBI:000000000000000000000000000000000902'
    },
    distributionTargets: {
      href:
        'http://localhost:8080/taskana/api/v1/workbaskets/WBI:000000000000000000000000000000000902/distribution-targets'
    },
    accessItems: {
      href:
        'http://localhost:8080/taskana/api/v1/workbaskets/WBI:000000000000000000000000000000000902/workbasketAccessItems'
    },
    allWorkbaskets: {
      href: 'http://localhost:8080/taskana/api/v1/workbaskets'
    },
    removeDistributionTargets: {
      href:
        'http://localhost:8080/taskana/api/v1/workbaskets/WBI:000000000000000000000000000000000902/distribution-targets'
    }
  }
};

export const workbasketReadStateMock = {
  selectedWorkbasket: selectedWorkbasketMock,
  paginatedWorkbasketsSummary: {
    _links: {
      self: {
        href:
          'http://localhost:8080/taskana/api/v1/workbaskets/?sort-by=name&order=asc&domain=DOMAIN_A&page=3&page-size=8'
      },
      first: {
        href:
          'http://localhost:8080/taskana/api/v1/workbaskets/?sort-by=name&order=asc&domain=DOMAIN_A&page-size=8&page=1'
      },
      last: {
        href:
          'http://localhost:8080/taskana/api/v1/workbaskets/?sort-by=name&order=asc&domain=DOMAIN_A&page-size=8&page=3'
      },
      prev: {
        href:
          'http://localhost:8080/taskana/api/v1/workbaskets/?sort-by=name&order=asc&domain=DOMAIN_A&page-size=8&page=2'
      }
    },
    workbaskets: [
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
        workbasketId: 'WBI:100000000000000000000000000000000007',
        key: 'USER-1-2',
        name: 'PPK User 2 KSC 1',
        domain: 'DOMAIN_A',
        type: 'PERSONAL',
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
      }
    ],
    page: {
      size: 8,
      totalElements: 21,
      totalPages: 3,
      number: 3
    }
  },
  action: ACTION.READ
};
