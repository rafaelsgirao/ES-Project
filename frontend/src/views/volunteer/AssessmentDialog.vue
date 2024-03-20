<template>
  <v-dialog v-model="dialog" persistent width="1300">
    <v-card>
      <v-card-title>
        <span 
        class="headline">
          {{ 'New Assessment' }}
        </span>
      </v-card-title>
      <v-card-text>
        <v-form 
        ref="form" 
        lazy-validation>
          <v-row>
            <v-text-field 
            label="Review" 
            v-model="newAssessment.review" 
            data-cy="newAssessmentInput"
            :rules="[
    (input) =>
      reviewHasAtLeastTenCharacters(input) ||
      'Review must be at least 10 characters long',]"
            ></v-text-field>
          </v-row>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn 
        variant="text" 
        @click="$emit('close-assessment-dialog')">
          Close
        </v-btn>
        <v-btn 
        variant="text" 
        data-cy="writeAssessment"
        :disabled="!reviewHasAtLeastTenCharacters(newAssessment.review)">
          Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script lang="ts">
import { Model, Component, Vue } from 'vue-property-decorator';
import Assessment from '@/models/assessment/Assessment';

@Component({})

export default class AssessmentDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;

  newAssessment: Assessment = new Assessment();

  reviewHasAtLeastTenCharacters(review: string) {
    if (review) {
      return review.length >= 10;
    }
  }

}
</script>

<style lang="scss" scoped>
