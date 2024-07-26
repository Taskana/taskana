import { messageTypes } from './message-types';

export const messageByErrorCode = {
  [messageTypes.ERROR]: {
    FALLBACK:
      'An error occurred, but there is no error code. Please contact your administrator to specify the error code.',
    CRITICAL_SYSTEM_ERROR: 'A system error occurred. Please contact your administrator.',
    UNKNOWN_ERROR: 'An unknown error occurred. Please contact your administrator.',

    ENTITY_NOT_UP_TO_DATE:
      'Cannot be saved because there has been a modification while editing. Please reload to get the current version.',
    DOMAIN_NOT_FOUND: 'Domain {domain} cannot be found',
    NOT_AUTHORIZED: 'Current user {currentUserId} is not authorized. User must be member of role(s) {roles}.',
    SPINNER_TIMEOUT: 'Request time exceeded. Please make sure you have internet connection.',
    HISTORY_EVENT_NOT_FOUND: 'History Event with id {historyEventId} cannot be found',
    PAYLOAD_TOO_LARGE: 'Maximum upload size was exceeded',
    CLASSIFICATION_SERVICE_LEVEL_MALFORMED:
      'Service level {serviceLevel} of Classification with key {classificationKey} and domain {domain} is invalid. ' +
      'The service level has to be a positive ISO-8601 duration format and only whole days are supported. ' +
      "The format must be 'PnD'.",
    INVALID_ARGUMENT: 'A method was called with an invalid argument.',
    EMPTY_FIELDS: 'There are empty fields which are required',
    OWNER_NOT_VALID: 'The {owner} introduced is not valid',

    CLASSIFICATION_IN_USE:
      'Classification with key {classificationKey} in domain {domain} cannot be deleted since there are Tasks associated with this Classification.',
    CLASSIFICATION_ALREADY_EXISTS:
      'Classification with key {classificationKey} cannot be saved since a Classification with the same key already exists in domain {domain}',
    CLASSIFICATION_WITH_ID_NOT_FOUND: 'Classification with id {classificationId} cannot be found',
    CLASSIFICATION_COPY_NOT_CREATED: 'Cannot copy a not created Classification',

    WORKBASKET_SAVE: 'The Workbasket cannot be saved since the Workbasket Information contains invalid values',
    WORKBASKET_WITH_ID_NOT_FOUND: 'Workbasket with id {workbasketId} cannot be found',
    WORKBASKET_WITH_KEY_NOT_FOUND: 'Workbasket with key {workbasketKey} cannot be found in domain {domain}',
    WORKBASKET_ALREADY_EXISTS:
      'Workbasket with key {workbasketKey} cannot be saved since a Workbasket with the same key already exists in domain {domain}',
    WORKBASKET_IN_USE: 'Workbasket with id {workbasketId} cannot be deleted since it contains non-completed Tasks',
    WORKBASKET_ACCESS_ITEM_ALREADY_EXISTS:
      'Workbasket Access Item with access id {accessId} for Workbasket with id {workbasketId} cannot be created since it already exists',
    NOT_AUTHORIZED_ON_WORKBASKET_WITH_ID:
      'Current user {currentUserId} has no permission for Workbasket with id {workbasketId}. Required permission(s): {requiredPermissions}.',
    NOT_AUTHORIZED_ON_WORKBASKET_WITH_KEY_AND_DOMAIN:
      'Current user {currentUserId} has no permission for Workbasket with key {workbasketKey}. Required permission(s): {requiredPermissions}.',

    TASK_ALREADY_EXISTS:
      'Task with external id {externalTaskId} cannot be created, because a Task with the same external id already exists.',
    TASK_NOT_FOUND: 'Task with id {taskId} cannot be found',
    TASK_INVALID_CALLBACK_STATE:
      'Callback state {taskCallbackState} for Task with id {taskId} is invalid. Required callback states: {requiredCallbackStates}',
    TASK_INVALID_OWNER: 'Current user {currentUserId} is not the owner of the Task with id {taskId}',
    TASK_INVALID_STATE: 'Task with id {taskId} is in state {taskState}. Required state(s): {requiredTaskStates}.',

    USER_ALREADY_EXISTS: 'User with id {userId} cannot be created, because a User with that id does already exist',
    USER_NOT_FOUND: 'User with id {userId} cannot be found',

    IMPORT_EXPORT_UPLOAD_FAILED: 'Upload failed. The uploaded file probably exceeded the maximum file size of 10 MB.',
    IMPORT_EXPORT_UPLOAD_FAILED_AUTH: 'Upload failed because you have no access to apply this operation.',
    IMPORT_EXPORT_UPLOAD_FAILED_NOT_FOUND: 'Upload failed because operation was not found',
    IMPORT_EXPORT_UPLOAD_FAILED_CONFLICTS: 'Upload failed because operation has conflicts',
    IMPORT_EXPORT_UPLOAD_FAILED_SIZE: 'Upload failed because maximum file size exceeded',
    IMPORT_EXPORT_UPLOAD_FILE_FORMAT: 'File format is not allowed. Please use a .json file.',

    SETTINGS_SAVE: 'Settings cannot be saved since the form contains invalid values.',
    SETTINGS_NO_SCHEMA:
      'Wrong data format of UI settings. The object must contain field "schema". Please contact your administrator.'
  },

  [messageTypes.SUCCESS]: {
    FALLBACK:
      'Action was completed successfully, but this success message was not configured properly. ' +
      'Please ask your administrator to configure this message.',

    CLASSIFICATION_CREATE: 'Classification with key {classificationKey} was created',
    CLASSIFICATION_UPDATE: 'Classification with key {classificationKey} was updated',
    CLASSIFICATION_REMOVE: 'Classification with key {classificationKey} was removed',
    CLASSIFICATION_MOVE: 'Classification with key {classificationKey} was moved',
    CLASSIFICATION_RESTORE: 'Classification restored',
    CLASSIFICATION_IMPORT: 'Classifications imported',

    WORKBASKET_CREATE: 'Workbasket with key {workbasketKey} was created',
    WORKBASKET_UPDATE: 'Workbasket with key {workbasketKey} was updated',
    WORKBASKET_REMOVE: 'Workbasket with key {workbasketKey} was removed',
    WORKBASKET_RESTORE: 'Workbasket restored',
    WORKBASKET_IMPORT: 'Workbaskets imported',
    WORKBASKET_ACCESS_ITEM_SAVE: 'Workbasket Access Items were saved',
    WORKBASKET_ACCESS_ITEM_RESTORE: 'Workbasket Access Items restored',
    WORKBASKET_DISTRIBUTION_TARGET_SAVE: 'Workbasket Distribution Targets were saved',
    WORKBASKET_DISTRIBUTION_TARGET_RESTORE: 'Workbasket Distribution Targets restored',
    WORKBASKET_DISTRIBUTION_TARGET_REMOVE:
      'Workbasket with key {workbasketKey} was removed as Workbasket Distribution Target',
    WORKBASKET_ACCESS_ITEM_REMOVE_PERMISSION: '{accessId} was removed',

    TASK_CREATE: 'Task with name {taskName} was created',
    TASK_UPDATE: 'Task with name {taskName} was updated',
    TASK_DELETE: 'Task with name {taskName} was deleted',
    TASK_RESTORE: 'Task restored',

    SETTINGS_SAVE: 'Settings were updated'
  },

  [messageTypes.INFORMATION]: {
    EMPTY_WORKBASKET: 'Selected Workbasket is empty'
  },

  [messageTypes.WARNING]: {
    PERM_EDIT_TASKS_MISSING_DEPENDING_PERMISSION:
      '"Edit tasks" permission was selected without the required "Read tasks" and "Read" permissions. ' +
      'Your changes will still be saved but they might lead to unexpected behavior.',
    PERM_READ_TASKS_MISSING_DEPENDING_PERMISSIONS:
      '"Read tasks" permission was selected without the required "Read" permission. ' +
      'Your changes will still be saved but they might lead to unexpected behavior.',
    PERM_OPEN_MISSING_DEPENDING_PERMISSIONS:
      '"Open" permission was selected without the required "Read tasks" and "Read" permissions. ' +
      'Your changes will still be saved but they might lead to unexpected behavior.',
    PERM_DISTRIBUTE_MISSING_DEPENDING_PERMISSIONS:
      '"Distribute" permission was selected without the required "Append" and "Transfer" permissions. ' +
      'Your changes will still be saved but they might lead to unexpected behavior.',
    REPORT_DATA_WRONG_HEADER:
      'The received header of the Report data does not match the expected header. ' +
      'The data might be displayed incorrectly. Please contact your administrator.',
    SETTINGS_INVALID_DATA:
      'The data structure is invalid. The setting {setting} is configured under UI settings but there is no matching ' +
      'attribute to save the value. Please contact your administrator.'
  },

  [messageTypes.DIALOG]: {
    POPUP_CONFIGURATION: 'This Popup was not configured properly for this request. Please contact your administrator.',

    WORKBASKET_DELETE: 'Delete Workbasket with key {workbasketKey}?',
    CLASSIFICATION_DELETE: 'Delete Classification with key {classificationKey}?',
    TASK_DELETE: 'Delete Task with id {taskId}?',
    ACCESS_ITEM_MANAGEMENT_REVOKE_ACCESS: 'Delete all access related to {accessId}?'
  }
};
