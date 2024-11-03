<script lang="ts">
  import Bar from "$lib/components/bar.svelte";
  import * as Table from "$lib/components/ui/table";
  import type { StatefulSet } from "$lib/model/StatefulSet";
  import { statefulSetAPI } from "$lib/service/statefulset-service";

  export let namespace;
  let loading = true;

  let statefulSets: StatefulSet[] = [];

  $: if (namespace) {
    getAllStatefulSets();
  }

  const getAllStatefulSets = async () => {
    try {
      loading = true;
      statefulSets = await statefulSetAPI.getAllStatefulSets(namespace);
    } finally  {
      loading = false;
    }
  }

</script>

<Table.Root>
<Table.Header>
 <Table.Row>
  <Table.Head>NAME</Table.Head>
  <Table.Head>READY</Table.Head>
  <Table.Head>AGE</Table.Head>
 </Table.Row>
</Table.Header>
<Table.Body>
  {#if (loading)}
  <Table.Row>
    <Table.Cell><Bar/></Table.Cell>
    <Table.Cell><Bar/></Table.Cell>
    <Table.Cell><Bar/></Table.Cell>
  </Table.Row>
  {:else}
  {#each statefulSets as statefulSet}
  <Table.Row>
    <Table.Cell>{statefulSet.name}</Table.Cell>
    <Table.Cell>{statefulSet.ready_replicas}/{statefulSet.total_replicas}</Table.Cell>
    <Table.Cell>{statefulSet.age}</Table.Cell>
 </Table.Row>
 {/each}
 {/if}
</Table.Body>
</Table.Root>