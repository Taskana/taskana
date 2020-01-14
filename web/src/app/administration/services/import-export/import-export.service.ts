import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';

@Injectable()
export class ImportExportService {
  public importingFinished = new Subject<boolean>();

  setImportingFinished(value: boolean) {
    this.importingFinished.next(value);
  }

  getImportingFinished(): Observable<boolean> {
    return this.importingFinished.asObservable();
  }
}
