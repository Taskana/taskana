export class SetSelectedClassificationType {
  static readonly type = '[Classification-Types-Selector] Set selected classification type';
  constructor(public selectedType: string) {
  }
}
