import { Customisation } from 'app/models/customisation';
import { State, NgxsOnInit, StateContext, Action } from '@ngxs/store';
import { ClassificationCategoriesService } from 'app/shared/services/classifications/classification-categories.service';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

class InitializeStore {
  static readonly type = '[EngineConfigurationState] Initializing state';
}

@State<EngineConfigurationStateModel>({ name: 'engineConfiguration' })
export class EngineConfigurationState implements NgxsOnInit {
  constructor(private categoryService: ClassificationCategoriesService) {
  }

  @Action(InitializeStore)
  initializeStore(ctx: StateContext<EngineConfigurationStateModel>): Observable<any> {
    return this.categoryService.getCustomisation().pipe(
      tap(customisation => ctx.setState({
        ...ctx.getState(),
        customisation,
        language: 'EN'
      })),
    );
  }

  ngxsOnInit(ctx: StateContext<EngineConfigurationStateModel>): void {
    ctx.dispatch(new InitializeStore());
  }
}


export interface EngineConfigurationStateModel {
  customisation: Customisation,
  language: string
}
