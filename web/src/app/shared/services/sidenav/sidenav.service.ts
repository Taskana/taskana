import { Injectable } from '@angular/core';
import { MatSidenav } from '@angular/material';

@Injectable({
  providedIn: 'root'
})
export class SidenavService {
  private sidenav: MatSidenav;
  state: boolean = false;

  public setSidenav(sidenav: MatSidenav) {
    this.sidenav = sidenav;
  }

  public toggle_sidenav(): void {
    this.sidenav.toggle();
    this.state = this.sidenav.opened;
  }
}
