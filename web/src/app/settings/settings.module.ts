import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SettingsComponent } from './components/Settings/settings.component';
import { SettingsRoutingModule } from './settings-routing.module';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { SettingsService } from './services/settings-service';

@NgModule({
  declarations: [SettingsComponent],
  imports: [
    CommonModule,
    SettingsRoutingModule,
    MatIconModule,
    MatTooltipModule,
    MatButtonModule,
    MatInputModule,
    FormsModule
  ],
  providers: [SettingsService]
})
export class SettingsModule {}
