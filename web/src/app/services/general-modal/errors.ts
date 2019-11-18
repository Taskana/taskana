import { ErrorModel } from '../../models/error-model';
import {Pair} from '../../models/pair';


export enum ERROR_TYPES {
  NO_AUTH,
  EXP_AUTH,
  NO_ACCESS,
}
// TODO: funktioniert unser Pair hierfür? -> Konstruktor checken!
export const errors = new Map<ERROR_TYPES, Pair> ([
  [ERROR_TYPES.NO_AUTH, new Pair(
    'Authentication required',
    'You need to be logged in to perform this action.'
  )],
  [ERROR_TYPES.EXP_AUTH, new Pair(
    'Authentication expired',
    'Your session has expired, log in to perform this action.'
  )],
  [ERROR_TYPES.NO_ACCESS, new Pair(
    'Access denied',
    'You have no permission to perform this action.'
  )],
]);
