import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UploadService {
  private currentProgressValue = new Subject<number>();
  public isInUse = false;

  setCurrentProgressValue(value: number) {
    this.currentProgressValue.next(value);
  }

  getCurrentProgressObservable(): Observable<number> {
    return this.currentProgressValue.asObservable();
  }
}
