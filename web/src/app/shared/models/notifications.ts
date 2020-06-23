import { Pair } from './pair';

export enum NOTIFICATION_TYPES {

  // ERRORS
  FETCH_ERR,
  FETCH_ERR_2,
  FETCH_ERR_3,
  FETCH_ERR_4,
  FETCH_ERR_5,
  FETCH_ERR_6,
  FETCH_ERR_7,
  DELETE_ERR,
  DELETE_ERR_2,
  CREATE_ERR,
  CREATE_ERR_2,
  REMOVE_ERR,
  REMOVE_ERR_2,
  SAVE_ERR,
  SAVE_ERR_2,
  SAVE_ERR_3,
  SAVE_ERR_4,
  SELECT_ERR,
  FILE_ERR,
  IMPORT_ERR_1,
  IMPORT_ERR_2,
  IMPORT_ERR_3,
  IMPORT_ERR_4,
  UPLOAD_ERR,
  TIMEOUT_ERR,
  GENERAL_ERR,
  ACCESS_ERR,
  MARK_ERR,

  // ALERTS
  // currently their names are used as a way to determine the type of the alert
  // e.g. we extract from 'SUCCESS_ALERT_2' in notification.service, that this is a success alert
  // and should therefore have the color green, so please __keep this in mind when refactoring__
  // usages of this undocumented sideffect: notification.service.ts and toast.component.ts
  INFO_ALERT,
  INFO_ALERT_2,
  DANGER_ALERT,
  DANGER_ALERT_2,
  SUCCESS_ALERT,
  SUCCESS_ALERT_2,
  SUCCESS_ALERT_3,
  SUCCESS_ALERT_4,
  SUCCESS_ALERT_5,
  SUCCESS_ALERT_6,
  SUCCESS_ALERT_7,
  SUCCESS_ALERT_8,
  SUCCESS_ALERT_9,
  SUCCESS_ALERT_10,
  SUCCESS_ALERT_11,
  SUCCESS_ALERT_12,
  SUCCESS_ALERT_13,
  SUCCESS_ALERT_14,
  WARNING_ALERT,
  WARNING_ALERT_2,
  WARNING_CANT_COPY
}

export const notifications = new Map<NOTIFICATION_TYPES, Pair>([
  // access-items-management.component.ts
  [NOTIFICATION_TYPES.FETCH_ERR, new Pair(
    'There was an error while retrieving your access ids with groups.',
    ''
  )],
  // access-items-management.component.ts
  [NOTIFICATION_TYPES.FETCH_ERR_2, new Pair(
    'There was an error while retrieving your access items ',
    ''
  )],
  // access-items-management.component.ts
  [NOTIFICATION_TYPES.DELETE_ERR, new Pair(
    'You can\'t delete a group',
    '',
  )],
  // classification-details.component
  [NOTIFICATION_TYPES.CREATE_ERR, new Pair(
    'There was an error while creating this classification',
    '',
  )],
  // classification-details.component
  [NOTIFICATION_TYPES.REMOVE_ERR, new Pair(
    'There was an error while removing your classification',
    ''
  )],
  // classification-details.component
  [NOTIFICATION_TYPES.SAVE_ERR, new Pair(
    'There was an error while saving your classification',
    ''
  )],
  // classification-details.component
  [NOTIFICATION_TYPES.SELECT_ERR, new Pair(
    'There is no classification selected',
    'Please check if you are creating a classification'
  )],
  // import-export.component
  [NOTIFICATION_TYPES.FILE_ERR, new Pair(
    'Wrong format',
    'This file format is not allowed! Please use a .json file.'
  )],
  // import-export.component
  [NOTIFICATION_TYPES.IMPORT_ERR_1, new Pair(
    'Import was not successful',
    'Import was not successful, you have no access to apply this operation.'
  )],
  // import-export.component
  [NOTIFICATION_TYPES.IMPORT_ERR_2, new Pair(
    'Import was not successful',
    'Import was not successful, operation was not found.'
  )],
  // import-export.component
  [NOTIFICATION_TYPES.IMPORT_ERR_3, new Pair(
    'Import was not successful',
    'Import was not successful, operation has some conflicts.'
  )],
  // import-export.component
  [NOTIFICATION_TYPES.IMPORT_ERR_4, new Pair(
    'Import was not successful',
    'Import was not successful, maximum file size exceeded.'
  )],
  // import-export.component
  [NOTIFICATION_TYPES.UPLOAD_ERR, new Pair(
    'Upload failed',
    `The upload didn't proceed sucessfully. 
    \n The uploaded file probably exceeded the maximum file size of 10 MB.`
  )],
  // taskdetails.component
  [NOTIFICATION_TYPES.FETCH_ERR_3, new Pair(
    '',
    'An error occurred while fetching the task'
  )],
  // workbasket-details.component
  [NOTIFICATION_TYPES.FETCH_ERR_4, new Pair(
    '',
    'An error occurred while fetching the workbasket'
  )],
  // access-items.component
  [NOTIFICATION_TYPES.SAVE_ERR_2, new Pair(
    'There was an error while saving your workbasket\'s access items',
    ''
  )],
  // workbaskets-distribution-targets.component
  [NOTIFICATION_TYPES.SAVE_ERR_3, new Pair(
    'There was an error while saving your workbasket\'s distribution targets',
    '',
  )],
  // workbasket-information.component
  [NOTIFICATION_TYPES.REMOVE_ERR_2, new Pair(
    'There was an error removing distribution target for {workbasketId}.',
    '',
  )],
  // workbasket-information.component
  [NOTIFICATION_TYPES.SAVE_ERR_4, new Pair(
    'There was an error while saving your workbasket',
    ''
  )],
  // workbasket-information.component
  [NOTIFICATION_TYPES.CREATE_ERR_2, new Pair(
    'There was an error while creating this workbasket',
    ''
  )],
  // workbasket-information.component
  [NOTIFICATION_TYPES.MARK_ERR, new Pair(
    'Workbasket was marked for deletion.',
    'The Workbasket {workbasketId} still contains completed tasks and could not be deleted.'
      + ' Instead is was marked for deletion and will be deleted automatically '
      + 'as soon as the completed tasks are cleared from the database.'
  )],
  // domain.guard
  [NOTIFICATION_TYPES.FETCH_ERR_5, new Pair(
    'There was an error, please contact your administrator',
    'There was an error getting Domains'
  )],
  // history.guard
  [NOTIFICATION_TYPES.FETCH_ERR_6, new Pair(
    'There was an error, please contact your administrator',
    'There was an error getting history provider'
  )],
  // http-client-interceptor.service
  [NOTIFICATION_TYPES.ACCESS_ERR, new Pair(
    'You have no access to this resource',
    ''
  )],
  // http-client-interceptor.service
  [NOTIFICATION_TYPES.GENERAL_ERR, new Pair(
    'There was an error, please contact your administrator',
    ''
  )],
  // spinner.component
  [NOTIFICATION_TYPES.TIMEOUT_ERR, new Pair(
    'There was an error with your request, please make sure you have internet connection',
    'Request time exceeded'
  )],
  // taskdetails.component
  [NOTIFICATION_TYPES.FETCH_ERR_7, new Pair(
    'An error occurred while fetching the task',
    ''
  )],
  // taskdetails.component
  [NOTIFICATION_TYPES.DELETE_ERR_2, new Pair(
    'An error occurred while deleting the task',
    ''
  )],

  // ALERTS

  // access-items-management.component
  [NOTIFICATION_TYPES.SUCCESS_ALERT, new Pair(
    '',
    '{accessId} was removed successfully'
  )],
  // classification-details.component
  [NOTIFICATION_TYPES.SUCCESS_ALERT_2, new Pair(
    '',
    'Classification {classificationKey} was created successfully'
  )],
  // classification-details.component
  [NOTIFICATION_TYPES.SUCCESS_ALERT_3, new Pair(
    '',
    'Classification {classificationKey} was saved successfully'
  )],
  // classification-details.component
  // access-items.component
  // workbasket.distribution-targets.component
  // workbasket-information.component
  // taskdetails.component
  [NOTIFICATION_TYPES.INFO_ALERT, new Pair(
    '',
    'Information restored'
  )],
  // classification-details.component
  [NOTIFICATION_TYPES.SUCCESS_ALERT_4, new Pair(
    '',
    'Classification {classificationKey} was removed successfully'
  )],
  // classification-list.component
  [NOTIFICATION_TYPES.SUCCESS_ALERT_5, new Pair(
    '',
    'Classification {classificationKey} was moved successfully'
  )],
  // import-export.component
  [NOTIFICATION_TYPES.SUCCESS_ALERT_6, new Pair(
    '',
    'Import was successful'
  )],
  // access-items.component
  [NOTIFICATION_TYPES.SUCCESS_ALERT_7, new Pair(
    '',
    'Workbasket {workbasketKey} Access items were saved successfully'
  )],
  // workbasket.distribution-targets.component
  [NOTIFICATION_TYPES.SUCCESS_ALERT_8, new Pair(
    '',
    'Workbasket {workbasketName} Distribution targets were saved successfully'
  )],
  // workbasket-information.component
  [NOTIFICATION_TYPES.SUCCESS_ALERT_9, new Pair(
    '',
    'DistributionTargets for workbasketID {workbasketId} was removed successfully'
  )],
  // workbasket-information.component
  [NOTIFICATION_TYPES.SUCCESS_ALERT_10, new Pair(
    '',
    'Workbasket {workbasketKey} was saved successfully'
  )],
  // workbasket-information.component
  [NOTIFICATION_TYPES.SUCCESS_ALERT_11, new Pair(
    '',
    'Workbasket {workbasketKey} was created successfully'
  )],
  // workbasket-information.component
  [NOTIFICATION_TYPES.SUCCESS_ALERT_12, new Pair(
    '',
    'The Workbasket {workbasketId} has been deleted.'
  )],
  // forms-validator.service
  [NOTIFICATION_TYPES.WARNING_ALERT, new Pair(
    '',
    'There are some empty fields which are required.'
  )],
  // forms-validator.service x2
  [NOTIFICATION_TYPES.WARNING_ALERT_2, new Pair(
    '',
    'The {owner} introduced is not valid.'
  )],
  // taskdetails.component
  [NOTIFICATION_TYPES.DANGER_ALERT, new Pair(
    '',
    'There was an error while updating.'
  )],
  // taskdetails.component
  [NOTIFICATION_TYPES.SUCCESS_ALERT_13, new Pair(
    '',
    'Task {taskId} was created successfully.'
  )],
  // taskdetails.component
  [NOTIFICATION_TYPES.SUCCESS_ALERT_14, new Pair(
    '',
    'Updating was successful.'
  )],
  // taskdetails.component
  [NOTIFICATION_TYPES.DANGER_ALERT_2, new Pair(
    '',
    'There was an error while creating a new task.'
  )],
  // task-master.component
  [NOTIFICATION_TYPES.INFO_ALERT_2, new Pair(
    '',
    'The selected Workbasket is empty!'
  )],
  [NOTIFICATION_TYPES.WARNING_CANT_COPY, new Pair(
    '',
    'Can\'t copy a not created classification'
  )]
]);
