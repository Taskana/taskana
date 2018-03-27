import { Injectable, HostListener } from '@angular/core';
import { Orientation } from 'app/models/orientation';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class OrientationService {

  private lock = false;
  private currentOrientation = undefined;
  public orientation = new BehaviorSubject<Orientation>(this.currentOrientation);

  constructor() { }

  onResize() {
    const orientation = this.detectOrientation();
    if (orientation !== this.currentOrientation) {
      this.currentOrientation = orientation;
      if (!this.lock) {
        this.lock = !this.lock;
        setTimeout(() => this.changeOrientation(orientation), 250);
      }
    }
  }

  getOrientation(): Observable<Orientation> {
    return this.orientation.asObservable();
  }

  changeOrientation(orientation: Orientation) {
    this.lock = !this.lock;
    if (this.currentOrientation) {
      this.orientation.next(orientation);
    }
  }

  private detectOrientation(): Orientation {
    if (window.innerHeight > window.innerWidth) {
      return Orientation.portrait;
    }
    return Orientation.landscape;
  }
}
