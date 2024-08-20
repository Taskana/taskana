import { AfterViewInit, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Chart } from 'chart.js';
import { ReportRow } from '../../models/report-row';
import { Select } from '@ngxs/store';
import { SettingsSelectors } from '../../../shared/store/settings-store/settings.selectors';
import { Observable, Subject } from 'rxjs';
import { Settings } from '../../../settings/models/settings';
import { takeUntil } from 'rxjs/operators';
import { SettingMembers } from '../../../settings/components/Settings/expected-members';

@Component({
  selector: 'kadai-monitor-canvas',
  templateUrl: './canvas.component.html',
  styleUrls: ['./canvas.component.scss']
})
export class CanvasComponent implements OnInit, AfterViewInit, OnDestroy {
  @Input() row: ReportRow;
  @Input() id: string;

  labels: string[];
  colors: string[];
  destroy$ = new Subject<void>();

  @Select(SettingsSelectors.getSettings) settings$: Observable<Settings>;

  ngOnInit() {
    this.settings$.pipe(takeUntil(this.destroy$)).subscribe((settings) => {
      this.setValuesFromSettings(settings);
    });
  }

  setValuesFromSettings(settings: Settings) {
    this.labels = [
      settings[SettingMembers.NameHighPriority],
      settings[SettingMembers.NameMediumPriority],
      settings[SettingMembers.NameLowPriority]
    ];
    this.colors = [
      settings[SettingMembers.ColorHighPriority],
      settings[SettingMembers.ColorMediumPriority],
      settings[SettingMembers.ColorLowPriority]
    ];
  }

  ngAfterViewInit() {
    const canvas = document.getElementById(this.id) as HTMLCanvasElement;
    if (canvas && this.id && this.row) {
      this.generateChart(this.id, this.row);
    }
  }

  generateChart(id: string, row: ReportRow) {
    const canvas = document.getElementById(id) as HTMLCanvasElement;
    new Chart(canvas, {
      type: 'doughnut',
      data: {
        labels: this.labels,
        datasets: [
          {
            label: 'Tasks by Priority',
            data: row.cells,
            backgroundColor: this.colors,
            borderWidth: 0
          }
        ]
      },
      options: {
        rotation: Math.PI,
        circumference: Math.PI,
        title: {
          display: true,
          text: String(row.total),
          position: 'bottom',
          fontSize: 18
        }
      }
    });
  }

  ngOnDestroy() {
    document.getElementById(this.id).outerHTML = ''; // destroy HTML element
    this.destroy$.next();
    this.destroy$.complete();
  }
}
