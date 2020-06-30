import { ClassificationSummary } from './classification-summary';
import { Links } from './links';

export interface Classification extends ClassificationSummary {
  isValidInDomain?: boolean;
  created?: string; // TODO: make this a Date
  modified?: string; // TODO: make this a Date
  description?: string;

  _links?: Links;
}
