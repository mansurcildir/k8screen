<script lang="ts">
  import Bar from "$lib/components/bar.svelte";
  import Terminal from "$lib/components/terminal.svelte";
  import * as Table from "$lib/components/ui/table";
  import type { Secret } from "$lib/model/Secret";
  import { secretAPI } from "$lib/service/secret-service";

  export let namespace;
  let loading = false;
  let loadingTable = false;
  let option: string = "DETAILS";
  let details: string;

  let secrets: Secret[] = [];
  let k8sItem: string;
  let open: boolean;

  $: if (namespace) {
    getAllSecrets();
  }

  const handleSecret = (secret: string) => {
    k8sItem = secret;
    open = true;
    getDetails();
  }

	const getAllSecrets = async () => {
		try {
      loadingTable = true;
      secrets = await secretAPI.getAllSecrets(namespace);
		} finally  {
      loadingTable = false;
		}
	}

  const getDetails = async (): Promise<string> => {
    loading = true;
    details = await secretAPI.getSecretDetails(namespace, k8sItem);
    loading = false;
    return details;
  };

</script>

<div class="flex flex-col" style="height: calc(100vh - 150px);">
  <div class="flex-grow overflow-auto">
<Table.Root>
  <Table.Header>
   <Table.Row>
    <Table.Head>NAME</Table.Head>
    <Table.Head>TYPE</Table.Head>
    <Table.Head>DATA</Table.Head>
    <Table.Head>AGE</Table.Head>
   </Table.Row>
  </Table.Header>
  <Table.Body>
    {#if (loadingTable)}
    <Table.Row>
      <Table.Cell><Bar/></Table.Cell>
      <Table.Cell><Bar/></Table.Cell>
      <Table.Cell><Bar/></Table.Cell>
      <Table.Cell><Bar/></Table.Cell>
    </Table.Row>
    {:else}
    {#each secrets as secret}
    <Table.Row on:click={() => {handleSecret(secret.name)}} class="cursor-pointer">
      <Table.Cell>{secret.name}</Table.Cell>
      <Table.Cell>{secret.type}</Table.Cell>
      <Table.Cell>{secret.data_size}</Table.Cell>
      <Table.Cell>{secret.age}</Table.Cell>
   </Table.Row>
   {/each}
   {/if}
  </Table.Body>
 </Table.Root>
</div>
 <Terminal {k8sItem} {option} {details} {loading} {open} {getDetails}/>
</div>
