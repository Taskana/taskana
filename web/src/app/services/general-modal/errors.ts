import { ErrorModel } from '../../models/error-model';
import {Pair} from '../../models/pair';


export enum ERROR_TYPES {
  DELETE_ERR_2,
  FETCH_ERR_7,
  NONE,

  // Mock Errors
  NO_AUTH,
  EXP_AUTH,
  NO_ACCESS,
  // Real Errors
  FETCH_ERR,
  FETCH_ERR_2,
  DELETE_ERR,
  CREATE_ERR,
  REMOVE_ERR,
  SAVE_ERR,
  SELECT_ERR,
  FILE_ERR,
  IMPORT_ERR_1,
  IMPORT_ERR_2,
  IMPORT_ERR_3,
  IMPORT_ERR_4,
  UPLOAD_ERR,
  FETCH_ERR_3,
  CREATE_ERR_2,
  SAVE_ERR_4,
  REMOVE_ERR_2,
  SAVE_ERR_3,
  SAVE_ERR_2,
  FETCH_ERR_4,
  TIMEOUT_ERR,
  GENERAL_ERR,
  HANDLE_ERR,
  FETCH_ERR_6,
  FETCH_ERR_5,
  MARK_ERR,

}

export enum ALERT_TYPES {
  INFO_ALERT_2,
  DANGER_ALERT_2,
  SUCCESS_ALERT_14,
  SUCCESS_ALERT_13,
  WARNING_ALERT_2,
  SUCCESS_ALERT_12,
  SUCCESS_ALERT_11,
  SUCCESS_ALERT_10,
  SUCCESS_ALERT_9,

  // TODO: check all alert models
  SUCCESS_ALERT,
  INFO_ALERT,
  WARNING_ALERT,
  DANGER_ALERT,
  SUCCESS_ALERT_8,
  SUCCESS_ALERT_7,
  SUCCESS_ALERT_6,
  SUCCESS_ALERT_5,
  SUCCESS_ALERT_4,
  SUCCESS_ALERT_3,
  SUCCESS_ALERT_2,

}

// TODO: funktioniert unser Pair hierfür? -> Konstruktor checken!
export const errors = new Map<ERROR_TYPES|ALERT_TYPES, Pair> ([
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
  // access-items-management.component.ts
  [ERROR_TYPES.FETCH_ERR, new Pair(
    '',
    'There was error while retrieving your access ids with groups.'
  )],
  // access-items-management.component.ts
  [ERROR_TYPES.FETCH_ERR_2, new Pair(
    '',
    'There was error while retrieving your access items '
  )],
  // access-items-management.component.ts
  [ERROR_TYPES.DELETE_ERR, new Pair(
    '',
    'You can\'t delete a group'
  )],
  // classification-details.component
  [ERROR_TYPES.CREATE_ERR, new Pair(
    '',
    'There was an error creating a classification'
  )],
  // classification-details.component
  [ERROR_TYPES.REMOVE_ERR, new Pair(
    '',
    'There was error while removing your classification'
  )],
  // classification-details.component
  [ERROR_TYPES.SAVE_ERR, new Pair(
    '',
    'There was error while saving your classification'
  )],
  // classification-details.component
  [ERROR_TYPES.SELECT_ERR, new Pair(
    'There is no classification selected',
    'Please check if you are creating a classification'
  )],
  // import-export.component
  [ERROR_TYPES.FILE_ERR, new Pair(
    '',
    'This file format is not allowed! Please use a .json file.'
  )],
  // import-export.component
  [ERROR_TYPES.IMPORT_ERR_1, new Pair(
    '',
    'Import was not successful, you have no access to apply this operation.'
  )],
  // import-export.component
  [ERROR_TYPES.IMPORT_ERR_2, new Pair(
    '',
    'Import was not successful, operation was not found.'
  )],
  // import-export.component
  [ERROR_TYPES.IMPORT_ERR_3, new Pair(
    '',
    'Import was not successful, operation has some conflicts.'
  )],
  // import-export.component
  [ERROR_TYPES.IMPORT_ERR_4, new Pair(
    '',
    'Import was not successful, maximum file size exceeded.'
  )],
  // import-export.component
  [ERROR_TYPES.UPLOAD_ERR, new Pair(
    'Upload failed',
    'The upload didn\'t proceed sucessfully. \
    \n Probably the uploaded file exceeded the maximum file size of 10 MB'
  )],
  // taskdetails.component
  [ERROR_TYPES.FETCH_ERR_3, new Pair(
    '',
    'An error occurred while fetching the task'
  )],
  // workbasket-details.component
  [ERROR_TYPES.FETCH_ERR_4, new Pair(
    '',
    'An error occurred while fetching the workbasket'
  )],
  // access-items.component
  [ERROR_TYPES.SAVE_ERR_2, new Pair(
    '',
    'There was error while saving your workbasket\'s access items'
  )],
  // workbaskets-distribution-targets.component
  [ERROR_TYPES.SAVE_ERR_3, new Pair(
    '',
    'There was error while saving your workbasket\'s distribution targets'
  )],
  // workbasket-information.component
  [ERROR_TYPES.REMOVE_ERR_2, new Pair(
    '',
    'There was an error removing distribution target for ${this.workbasket.workbasketId}.'
  )],
  // workbasket-information.component
  [ERROR_TYPES.SAVE_ERR_4, new Pair(
    '',
    'There was error while saving your workbasket'
  )],
  // workbasket-information.component
  [ERROR_TYPES.CREATE_ERR_2, new Pair(
    '',
    'There was an error creating a workbasket'
  )],
  // workbasket-information.component
  [ERROR_TYPES.MARK_ERR, new Pair(
    'There was an error marking workbasket for deletion',
    'It not possible to mark the workbasket for deletion, It has been deleted.'
  )],
  // domain.guard
  [ERROR_TYPES.FETCH_ERR_5, new Pair(
    'There was an error, please contact with your administrator',
    'There was an error getting Domains'
  )],
  // history.guard
  [ERROR_TYPES.FETCH_ERR_6, new Pair(
    'There was an error, please contact with your administrator',
    'There was an error getting history provider'
  )],
  // http-client-interceptor.component
  [ERROR_TYPES.HANDLE_ERR, new Pair(
    'You have no access to this resource ',
    ''
  )],
  // http-client-interceptor.component
  [ERROR_TYPES.GENERAL_ERR, new Pair(
    'There was error, please contact with your administrator',
    ''
  )],
  // http-client-interceptor.component
  [ERROR_TYPES.NONE, new Pair(
    'Error wird ignoriert, keine Message geworfen',
    ''
  )],
  // spinner.component
  [ERROR_TYPES.TIMEOUT_ERR, new Pair(
    'There was an error with your request, please make sure you have internet connection',
    'Request time execeed'
  )],
  // taskdetails.component
  [ERROR_TYPES.FETCH_ERR_7, new Pair(
    'An error occurred while fetching the task',
    ''
  )],
  // taskdetails.component
  [ERROR_TYPES.DELETE_ERR_2, new Pair(
    'An error occurred while deleting the task',
    ''
  )],

  // ALERTS

  // access-items-management.component
  [ALERT_TYPES.SUCCESS_ALERT, new Pair(
    '${this.accessIdSelected} was removed successfully',
    ''
  )],
  // classification-details.component
  [ALERT_TYPES.SUCCESS_ALERT_2, new Pair(
    'Classification ${classification.key} was saved successfully',
    ''
  )],
  // classification-details.component
  [ALERT_TYPES.SUCCESS_ALERT_3, new Pair(
    'Classification ${this.classification.key} was saved successfully',
    ''
  )],
  // classification-details.component
  // access-items.component
  // distribution-targets.component
  // workbasket-information.component
  [ALERT_TYPES.INFO_ALERT, new Pair(
    'Reset edited fields',
    ''
  )],
  // classification-details.component
  [ALERT_TYPES.SUCCESS_ALERT_4, new Pair(
    'Classification ${key} was removed successfully',
    ''
  )],
  // classification-list.component
  [ALERT_TYPES.SUCCESS_ALERT_5, new Pair(
    'Classification ${key} was saved successfully',
    ''
  )],
  // import-export.component
  [ALERT_TYPES.SUCCESS_ALERT_6, new Pair(
    'Import was successful',
    ''
  )],
  // access-items.component
  [ALERT_TYPES.SUCCESS_ALERT_7, new Pair(
    'Workbasket  ${component.workbasket.key} Access items were saved successfully',
    ''
  )],
  // distribution-targets.component
  [ALERT_TYPES.SUCCESS_ALERT_8, new Pair(
    'Workbasket  ${this.workbasket.name} : Distribution targets were saved successfully',
    ''
  )],
  // workbasket-information.component
  [ALERT_TYPES.SUCCESS_ALERT_9, new Pair(
    'DistributionTarget for workbasketID: ${this.workbasket.workbasketId} was removed successfully',
    ''
  )],
  // workbasket-information.component
  [ALERT_TYPES.SUCCESS_ALERT_10, new Pair(
    'Workbasket ${workbasketUpdated.key} was saved successfully',
    ''
  )],
  // workbasket-information.component
  [ALERT_TYPES.SUCCESS_ALERT_11, new Pair(
    'Workbasket ${workbasketUpdated.key} was created successfully',
    ''
  )],
  // workbasket-information.component
  [ALERT_TYPES.SUCCESS_ALERT_12, new Pair(
    'The Workbasket ${this.workbasket.workbasketId} has been marked for deletion',
    ''
  )],
  // forms-validator.service
  [ALERT_TYPES.WARNING_ALERT, new Pair(
    'There are some empty fields which are required.',
    ''
  )],
  // forms-validator.service x2
  [ALERT_TYPES.WARNING_ALERT_2, new Pair(
    'The ${responseOwner.field} introduced is not valid.',
    ''
  )],
  // taskdetails.component TODO: is dis error
  [ALERT_TYPES.DANGER_ALERT, new Pair(
    'There was an error while updating.',
    ''
  )],
  // taskdetails.component
  [ALERT_TYPES.SUCCESS_ALERT_13, new Pair(
    'Task ${this.currentId} was created successfully.',
    ''
  )],
  // taskdetails.component
  [ALERT_TYPES.SUCCESS_ALERT_14, new Pair(
    'Updating was successful.',
    ''
  )],
  // taskdetails.component
  [ALERT_TYPES.DANGER_ALERT_2, new Pair(
    'There was an error while creating a new task.',
    ''
  )],
  // task-master.component
  [ALERT_TYPES.INFO_ALERT_2, new Pair(
    'The selected Workbasket is empty!',
    ''
  )],
]);
