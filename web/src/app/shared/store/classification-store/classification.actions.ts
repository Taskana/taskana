import { Classification } from '../../models/classification';

export class SetSelectedClassificationType {
  static readonly type = '[Classification-Selected Type] Set selected classification type';
  constructor(public selectedType: string) {}
}

export class SelectClassification {
  static readonly type = '[Classification] Select a classification';
  constructor(public classificationId: string) {}
}

export class DeselectClassification {
  static readonly type = '[Classification] Deselect a classification';
}

export class CreateClassification {
  static readonly type = '[Classification] Create a new classification';
}

export class CopyClassification {
  static readonly type = '[Classification] Copy a classification';
}

export class SaveCreatedClassification {
  static readonly type = '[Classification] Save a classification that has been newly created or copied';
  constructor(public classification: Classification) {}
}

export class SaveModifiedClassification {
  static readonly type = '[Classification] Save an existing classification that has been modified';
  constructor(public classification: Classification) {}
}

export class RestoreSelectedClassification {
  static readonly type = '[Classification] Fetch and restore a classification';
  constructor(public classificationId: string) {}
}

export class RemoveSelectedClassification {
  static readonly type = '[Classification] Remove the selected Classification';
}

export class GetClassifications {
  static readonly type = '[Classification] Get all classifications';
}

export class UpdateClassification {
  static readonly type = '[Tree] Update a classification and refetch all classifications';
  constructor(public classification: Classification) {}
}
