import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminItemsComponent } from './admin-items.component';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { NgbPagination } from '@ng-bootstrap/ng-bootstrap';

describe('AdminItemsComponent', () => {
  let component: AdminItemsComponent;
  let fixture: ComponentFixture<AdminItemsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AdminItemsComponent],
      imports: [RouterModule.forRoot([]),
        FormsModule,
        NgbPagination],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AdminItemsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
