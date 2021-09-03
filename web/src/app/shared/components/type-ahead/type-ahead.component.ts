import { Component, EventEmitter, Input, OnDestroy, OnInit, Output, SimpleChanges } from '@angular/core';
import { AccessIdsService } from '../../services/access-ids/access-ids.service';
import { Observable, Subject } from 'rxjs';
import { FormControl, FormGroup } from '@angular/forms';
import { AccessId } from '../../models/access-id';
import { take, takeUntil } from 'rxjs/operators';
import { Select } from '@ngxs/store';
import { WorkbasketSelectors } from '../../store/workbasket-store/workbasket.selectors';
import { ButtonAction } from '../../../administration/models/button-action';

@Component({
  selector: 'taskana-shared-type-ahead',
  templateUrl: './type-ahead.component.html',
  styleUrls: ['./type-ahead.component.scss']
})
export class TypeAheadComponent implements OnInit, OnDestroy {
  @Input() savedAccessId;
  @Input() placeHolderMessage;
  @Input() entityId;
  @Input() isRequired = false;
  @Input() isDisabled = false;
  @Input() displayError = false;

  @Output() accessIdEventEmitter = new EventEmitter<AccessId>();
  @Output() isFormValid = new EventEmitter<boolean>();

  @Select(WorkbasketSelectors.buttonAction)
  buttonAction$: Observable<ButtonAction>;

  name: string = '';
  lastSavedAccessId: string = '';
  filteredAccessIds: AccessId[] = [];
  destroy$ = new Subject<void>();
  accessIdForm = new FormGroup({
    accessId: new FormControl('')
  });
  emptyAccessId: AccessId = { accessId: '', name: '' };

  constructor(private accessIdService: AccessIdsService) {}

  ngOnChanges(changes: SimpleChanges) {
    // currently needed because when saving, workbasket-details components sends old workbasket which reverts changes in this component
    if (changes.entityId) {
      this.setAccessIdFromInput();
    }
  }

  ngOnInit() {
    if (this.isDisabled) {
      this.accessIdForm.controls['accessId'].disable();
    }

    // currently needed because this component cannot obtain changes of the current workbasket from workbasket-information component
    this.buttonAction$.pipe(takeUntil(this.destroy$)).subscribe((button) => {
      if (button == ButtonAction.UNDO) {
        this.accessIdForm.controls['accessId'].setValue(this.lastSavedAccessId);
      }
    });

    this.setAccessIdFromInput();
  }

  ngAfterViewInit() {
    this.accessIdForm.controls['accessId'].valueChanges.pipe(takeUntil(this.destroy$)).subscribe(() => {
      const value = this.accessIdForm.controls['accessId'].value;
      if (value === '') {
        this.handleEmptyAccessId();
        return;
      }
      this.searchForAccessId(value);
    });
  }

  handleEmptyAccessId() {
    this.name = '';
    this.isFormValid.emit(!this.isRequired);
    if (this.placeHolderMessage !== 'Search for AccessId') {
      this.accessIdEventEmitter.emit(this.emptyAccessId);
    }
    if (this.isRequired) {
      this.accessIdForm.controls['accessId'].setErrors({ incorrect: true });
    }
  }

  searchForAccessId(value: string) {
    this.accessIdService
      .searchForAccessId(value)
      .pipe(take(1))
      .subscribe((accessIds) => {
        this.filteredAccessIds = accessIds;
        const accessId = accessIds.find((accessId) => accessId.accessId === value);

        if (typeof accessId !== 'undefined') {
          this.name = accessId?.name;
          this.isFormValid.emit(true);
          this.accessIdEventEmitter.emit(accessId);
        } else if (this.displayError) {
          this.isFormValid.emit(false);
          this.accessIdEventEmitter.emit(this.emptyAccessId);
          this.accessIdForm.controls['accessId'].setErrors({ incorrect: true });
        }
      });
  }

  setAccessIdFromInput() {
    const accessId = this.savedAccessId?.value;
    const access = accessId?.accessId || accessId?.accessId == '' ? accessId.accessId : this.savedAccessId || '';
    this.accessIdForm.controls['accessId'].setValue(access);
    this.lastSavedAccessId = access;
    this.name = accessId?.accessName || '';
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
