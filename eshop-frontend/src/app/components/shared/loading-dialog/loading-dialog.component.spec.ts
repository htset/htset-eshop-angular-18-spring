import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoadingDialogComponent } from './loading-dialog.component';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('LoadingDialogComponent', () => {
  let component: LoadingDialogComponent;
  let fixture: ComponentFixture<LoadingDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoadingDialogComponent],
      imports: [RouterModule.forRoot([]),
        FormsModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(LoadingDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
