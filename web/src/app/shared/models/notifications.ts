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

export const notifications = new Map<NOTIFICATION_TYPES, Pair<string, string>>([
  // access-items-management.component.ts
  [
    NOTIFICATION_TYPES.FETCH_ERR,
    { left: 'There was an error while retrieving your access ids with groups.', right: '' }
  ],
  // access-items-management.component.ts
  [NOTIFICATION_TYPES.FETCH_ERR_2, { left: 'There was an error while retrieving your access items ', right: '' }],
  // access-items-management.component.ts
  [NOTIFICATION_TYPES.DELETE_ERR, { left: "You can't delete a group", right: '' }],
  // classification-details.component
  [NOTIFICATION_TYPES.CREATE_ERR, { left: 'There was an error while creating this classification', right: '' }],
  // classification-details.component
  [NOTIFICATION_TYPES.REMOVE_ERR, { left: 'There was an error while removing your classification', right: '' }],
  // classification-details.component
  [NOTIFICATION_TYPES.SAVE_ERR, { left: 'There was an error while saving your classification', right: '' }],
  // classification-details.component
  [
    NOTIFICATION_TYPES.SELECT_ERR,
    { left: 'There is no classification selected', right: 'Please check if you are creating a classification' }
  ],
  // import-export.component
  [
    NOTIFICATION_TYPES.FILE_ERR,
    { left: 'Wrong format', right: 'This file format is not allowed! Please use a .json file.' }
  ],
  // import-export.component
  [
    NOTIFICATION_TYPES.IMPORT_ERR_1,
    {
      left: 'Import was not successful',
      right: 'Import was not successful, you have no access to apply this operation.'
    }
  ],
  // import-export.component
  [
    NOTIFICATION_TYPES.IMPORT_ERR_2,
    { left: 'Import was not successful', right: 'Import was not successful, operation was not found.' }
  ],
  // import-export.component
  [
    NOTIFICATION_TYPES.IMPORT_ERR_3,
    { left: 'Import was not successful', right: 'Import was not successful, operation has some conflicts.' }
  ],
  // import-export.component
  [
    NOTIFICATION_TYPES.IMPORT_ERR_4,
    { left: 'Import was not successful', right: 'Import was not successful, maximum file size exceeded.' }
  ],
  // import-export.component
  [
    NOTIFICATION_TYPES.UPLOAD_ERR,
    {
      left: 'Upload failed',
      right: `The upload didn't proceed sucessfully.
    \n The uploaded file probably exceeded the maximum file size of 10 MB.`
    }
  ],
  // taskdetails.component
  [NOTIFICATION_TYPES.FETCH_ERR_3, { left: '', right: 'An error occurred while fetching the task' }],
  // workbasket-details.component
  [NOTIFICATION_TYPES.FETCH_ERR_4, { left: '', right: 'An error occurred while fetching the workbasket' }],
  // access-items.component
  [
    NOTIFICATION_TYPES.SAVE_ERR_2,
    { left: "There was an error while saving your workbasket's access items", right: '' }
  ],
  // workbaskets-distribution-targets.component
  [
    NOTIFICATION_TYPES.SAVE_ERR_3,
    { left: "There was an error while saving your workbasket's distribution targets", right: '' }
  ],
  // workbasket-information.component
  [
    NOTIFICATION_TYPES.REMOVE_ERR_2,
    { left: 'There was an error removing distribution target for {workbasketId}.', right: '' }
  ],
  // workbasket-information.component
  [NOTIFICATION_TYPES.SAVE_ERR_4, { left: 'There was an error while saving your workbasket', right: '' }],
  // workbasket-information.component
  [NOTIFICATION_TYPES.CREATE_ERR_2, { left: 'There was an error while creating this workbasket', right: '' }],
  // workbasket-information.component
  [
    NOTIFICATION_TYPES.MARK_ERR,
    {
      left: 'Workbasket was marked for deletion.',
      right:
        'The Workbasket {workbasketId} still contains completed tasks and could not be deleted.' +
        ' Instead is was marked for deletion and will be deleted automatically ' +
        'as soon as the completed tasks are cleared from the database.'
    }
  ],
  // domain.guard
  [
    NOTIFICATION_TYPES.FETCH_ERR_5,
    { left: 'There was an error, please contact your administrator', right: 'There was an error getting Domains' }
  ],
  // history.guard
  [
    NOTIFICATION_TYPES.FETCH_ERR_6,
    {
      left: 'There was an error, please contact your administrator',
      right: 'There was an error getting history provider'
    }
  ],
  // http-client-interceptor.service
  [NOTIFICATION_TYPES.ACCESS_ERR, { left: 'You have no access to this resource', right: '' }],
  // http-client-interceptor.service
  [NOTIFICATION_TYPES.GENERAL_ERR, { left: 'There was an error, please contact your administrator', right: '' }],
  // spinner.component
  [
    NOTIFICATION_TYPES.TIMEOUT_ERR,
    {
      left: 'There was an error with your request, please make sure you have internet connection',
      right: 'Request time exceeded'
    }
  ],
  // taskdetails.component
  [NOTIFICATION_TYPES.FETCH_ERR_7, { left: 'An error occurred while fetching the task', right: '' }],
  // taskdetails.component
  [NOTIFICATION_TYPES.DELETE_ERR_2, { left: 'An error occurred while deleting the task', right: '' }],

  // ALERTS

  // access-items-management.component
  [NOTIFICATION_TYPES.SUCCESS_ALERT, { left: '', right: '{accessId} was removed successfully' }],
  // classification-details.component
  [
    NOTIFICATION_TYPES.SUCCESS_ALERT_2,
    { left: '', right: 'Classification {classificationKey} was created successfully' }
  ],
  // classification-details.component
  [
    NOTIFICATION_TYPES.SUCCESS_ALERT_3,
    { left: '', right: 'Classification {classificationKey} was saved successfully' }
  ],
  // classification-details.component
  // access-items.component
  // workbasket.distribution-targets.component
  // workbasket-information.component
  // taskdetails.component
  [NOTIFICATION_TYPES.INFO_ALERT, { left: '', right: 'Information restored' }],
  // classification-details.component
  [
    NOTIFICATION_TYPES.SUCCESS_ALERT_4,
    { left: '', right: 'Classification {classificationKey} was removed successfully' }
  ],
  // classification-list.component
  [
    NOTIFICATION_TYPES.SUCCESS_ALERT_5,
    { left: '', right: 'Classification {classificationKey} was moved successfully' }
  ],
  // import-export.component
  [NOTIFICATION_TYPES.SUCCESS_ALERT_6, { left: '', right: 'Import was successful' }],
  // access-items.component
  [
    NOTIFICATION_TYPES.SUCCESS_ALERT_7,
    { left: '', right: 'Workbasket {workbasketKey} Access items were saved successfully' }
  ],
  // workbasket.distribution-targets.component
  [
    NOTIFICATION_TYPES.SUCCESS_ALERT_8,
    { left: '', right: 'Workbasket {workbasketName} Distribution targets were saved successfully' }
  ],
  // workbasket-information.component
  [
    NOTIFICATION_TYPES.SUCCESS_ALERT_9,
    { left: '', right: 'DistributionTargets for workbasketID {workbasketId} was removed successfully' }
  ],
  // workbasket-information.component
  [NOTIFICATION_TYPES.SUCCESS_ALERT_10, { left: '', right: 'Workbasket {workbasketKey} was saved successfully' }],
  // workbasket-information.component
  [NOTIFICATION_TYPES.SUCCESS_ALERT_11, { left: '', right: 'Workbasket {workbasketKey} was created successfully' }],
  // workbasket-information.component
  [NOTIFICATION_TYPES.SUCCESS_ALERT_12, { left: '', right: 'The Workbasket {workbasketId} has been deleted.' }],
  // forms-validator.service
  [NOTIFICATION_TYPES.WARNING_ALERT, { left: '', right: 'There are some empty fields which are required.' }],
  // forms-validator.service x2
  [NOTIFICATION_TYPES.WARNING_ALERT_2, { left: '', right: 'The {owner} introduced is not valid.' }],
  // taskdetails.component
  [NOTIFICATION_TYPES.DANGER_ALERT, { left: '', right: 'There was an error while updating.' }],
  // taskdetails.component
  [NOTIFICATION_TYPES.SUCCESS_ALERT_13, { left: '', right: 'Task {taskId} was created successfully.' }],
  // taskdetails.component
  [NOTIFICATION_TYPES.SUCCESS_ALERT_14, { left: '', right: 'Updating was successful.' }],
  // taskdetails.component
  [NOTIFICATION_TYPES.DANGER_ALERT_2, { left: '', right: 'There was an error while creating a new task.' }],
  // task-master.component
  [NOTIFICATION_TYPES.INFO_ALERT_2, { left: '', right: 'The selected Workbasket is empty!' }],
  [NOTIFICATION_TYPES.WARNING_CANT_COPY, { left: '', right: "Can't copy a not created classification" }]
]);
