import { Injectable } from '@angular/core';
import { Orientation } from 'app/shared/models/orientation';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable()
export class OrientationService {
  private lock = false;
  private currentOrientation = Orientation.landscape;
  public orientation = new BehaviorSubject<Orientation>(this.currentOrientation);

  private static detectOrientation(): Orientation {
    if (window.innerHeight > window.innerWidth) {
      return Orientation.portrait;
    }
    return Orientation.landscape;
  }

  onResize() {
    const orientation = OrientationService.detectOrientation();
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

  calculateNumberItemsList(
    heightContainer: number,
    cardHeight: number,
    unusedHeight: number,
    doubleList = false
  ): number {
    let cards = Math.round((heightContainer - unusedHeight) / cardHeight);
    if (doubleList && window.innerWidth < 992) {
      cards = Math.floor(cards / 2);
    }
    return cards;
  }
}
