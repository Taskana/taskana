import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UploadService {

  private currentProgressValue = new Subject<number>();
  public isInUse = false;

  constructor() { }

  setCurrentProgressValue(value: number) {
    this.currentProgressValue.next(value);
  }

  getCurrentProgressValue() {
    return this.currentProgressValue.asObservable();
  }

}
