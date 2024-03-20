<template>
  <v-dialog v-model="dialog" persistent width="1300">
    <v-card>
        <v-card-title>
           <span class="headline">
            {{ 'Select Participant' }}
          </span>
        </v-card-title>
    </v-card>
    <v-card-text>
      <v-form ref="form" lazy-validation>
        <v-row>
          <v-text-field
            label="Rating"
            :rules="[
              (v) =>
                validateRating(v) ||
                'Rating between 1 and 5 is required',
            ]"
            v-model="selectParticipant.rating"
            data-cy="ratingInput"
          ></v-text-field>
        </v-row>
      </v-form>
    </v-card-text>
    <v-card-actions>
      <v-spacer></v-spacer>
      <v-btn
        color="blue darken-1"
        variant="text"
        @click="$emit('close-participation-selection-dialog')"
      >
        Close
      </v-btn>
      <v-btn
        color="blue darken-1"
        variant="text"
        @click="createParticipation"
        data-cy="selectParticipant"
      >
        Make Participant
      </v-btn>
    </v-card-actions>
  </v-dialog>
</template>
<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Participation from '@/models/participation/Participation';
import RemoteServices from '@/services/RemoteServices';

@Component({})

export default class ParticipationSelectionDialog extends Vue {
  dialog: boolean = false;

  selectParticipant: Participation = new Participation();

  validateRating(value: any) {
    return true;
  }

  createParticipation() {
    
  }
}
</script>

<style scoped lang="scss">
