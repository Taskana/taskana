import { ReportData } from '../../models/report-data';

export const workbasketReportMock: ReportData = {
  meta: {
    name: 'WorkbasketPriorityReport',
    date: '2021-08-24T11:44:34.023901Z',
    header: ['<249', '250 - 500', '>501'],
    rowDesc: ['WORKBASKET'],
    sumRowDesc: 'Total'
  },
  rows: [
    {
      cells: [5, 0, 0],
      total: 5,
      depth: 0,
      desc: ['ADMIN'],
      display: true
    },
    {
      cells: [3, 5, 2],
      total: 10,
      depth: 0,
      desc: ['GPK_KSC'],
      display: true
    }
  ],
  sumRow: [
    {
      cells: [8, 5, 2],
      total: 15,
      depth: 0,
      desc: ['Total'],
      display: true
    }
  ]
};
