<script lang="ts">
  import Bar from "$lib/components/bar.svelte";
  import * as Table from "$lib/components/ui/table";
  import type { Pod } from "$lib/model/Pod";
  import { podAPI } from "$lib/service/pod-service";
  import * as Collapsible from "$lib/components/ui/collapsible/index.js";
  import Spinner from "$lib/components/spinner.svelte";
  import ChevronRight from "lucide-svelte/icons/chevron-right";
  import Button from "$lib/components/ui/button/button.svelte";
  import SquareTerminal from "lucide-svelte/icons/square-terminal";
  import CodeXML from "lucide-svelte/icons/code-xml";

  export let namespace;
  let loading = false;
  let logIsLoading = false;
  let cmdIsLoading = false;
  let option: string = "LOGS";
  let cmdReq: string;
  let cmdRes: string;

  let pods: Pod[] = [];
  let currentPod: string;
  let logs: string;
  let open: boolean;
  let containerHeight = 300; // Başlangıç yüksekliği
  const maxContainerHeight = 500; // Maksimum yükseklik sınırı
  const minContainerHeight = 100; // Minimum yükseklik sınırı

  $: if (namespace) {
    getAllPods();
  }

  const getAllPods = async () => {
    try {
      loading = true;
      pods = await podAPI.getAllPods(namespace);
    } finally {
      loading = false;
    }
  };

  const getPodLogs = async (podName: string) => {
    logIsLoading = true;
    try {
      logs = await podAPI.getPodLogs(namespace, podName);
      currentPod = podName;
      open = true;
    } finally {
      logIsLoading = false;
    }
  };

  const exec = async (podName: string, cmdReq: string) => {
    try {
      cmdIsLoading = true;
      cmdRes = await podAPI.exec(namespace, podName, ["sh", "-c", cmdReq]);
    } finally {
      cmdIsLoading = false;
    }
  }

  // Drag to resize functionality
  let isResizing = false;

  const handleMouseDown = () => {
    isResizing = true;
  };

  const handleMouseMove = (event: MouseEvent) => {
    if (isResizing) {
      const newHeight = containerHeight - event.movementY;

      // Eğer yeni yükseklik, maksimum yüksekliği geçmiyorsa ve minimum yüksekliğin altında değilse ayarlıyoruz
      if (newHeight <= maxContainerHeight && newHeight >= minContainerHeight) {
        // Yüksekliği artırma koşulu
        if (newHeight < containerHeight) {
          // Yukarı doğru kaydırma engeli
          const isAtTop = window.innerHeight - (containerHeight - event.movementY) <= 150; // Değiştirilebilir yükseklik sınırı
          if (!isAtTop) {
            containerHeight = newHeight;
          }
        } else {
          containerHeight = newHeight;
        }
      }
    }
  };

  const handleMouseUp = () => {
    isResizing = false;
  };

  // Mouse event listener'larını pencereden çıkartıyoruz
  window.addEventListener('mousemove', handleMouseMove);
  window.addEventListener('mouseup', handleMouseUp);
</script>

<div class="flex flex-col" style="height: calc(100vh - 150px);">
  <div class="flex-grow overflow-auto">
    <Table.Root>
      <Table.Header>
        <Table.Row>
          <Table.Head>NAME</Table.Head>
          <Table.Head>READY</Table.Head>
          <Table.Head>STATUS</Table.Head>
          <Table.Head>RESTARTS</Table.Head>
          <Table.Head>AGE</Table.Head>
        </Table.Row>
      </Table.Header>
      <Table.Body>
        {#if loading}
          <Table.Row>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
          </Table.Row>
        {:else}
          {#each pods as pod}
            <Table.Row on:click={() => getPodLogs(pod.name)} class="cursor-pointer">
              <Table.Cell>{pod.name}</Table.Cell>
              <Table.Cell>{pod.ready_containers}</Table.Cell>
              <Table.Cell>{pod.status}</Table.Cell>
              <Table.Cell>{pod.restarts}</Table.Cell>
              <Table.Cell>{pod.age}</Table.Cell>
            </Table.Row>
          {/each}
        {/if}
      </Table.Body>
    </Table.Root>
  </div>

  <Collapsible.Root class="group/collapsible rounded-t-md bg-sidebar" {open}>
    <div class="flex items-center px-3 py-1 gap-2">
      <Collapsible.Trigger>
        <ChevronRight
          class="ml-auto transition-transform duration-200 group-data-[state=open]/collapsible:rotate-90 size-4"
        />
      </Collapsible.Trigger>
      <h1 style="font-family: 'Courier New', Courier, monospace;">{currentPod} {option == "LOGS" ? "[logs]" : "[sh]"} </h1>
      
      {#if (currentPod)}
      <Button
      variant="ghost"
      class="ms-auto bg-muted rounded-lg p-2 h-auto"
      aria-label="Playground"
      onclick={() => {option = "LOGS"; getPodLogs(currentPod)}}
      >
        <CodeXML class="size-5" />
      </Button>

      <Button
      variant="ghost"
      class="bg-muted rounded-lg p-2 h-auto"
      aria-label="Playground"
      onclick={() => {option = "TERMINAL"}}
      >
        <SquareTerminal class="size-5" />
      </Button>
      {/if}

    </div>

    <Collapsible.Content>
      <div class="resize-handle" on:mousedown={handleMouseDown} style="cursor: ns-resize; height: 5px; background: gray; left: 0; right: 0;"></div>
      <div class="relative">
        <div class="bg-black text-white log-container overflow-auto rounded-b-md p-5" style="height: {containerHeight}px;">
          {#if (option === "LOGS" && logIsLoading)}
          <div class="flex justify-center items-center h-full">
            <Spinner />
          </div>
          {:else if (option === "LOGS")}
          {logs}
          {:else if (option === "TERMINAL" && cmdIsLoading)}
          <div class="flex justify-center items-center h-full">
            <Spinner />
          </div>
          {:else if (option === "TERMINAL")}
          <div class="flex flex-col justify-between h-full">
            {cmdRes}
            <div class="flex items-center w-full">
              > 
              <form on:submit={() => exec(currentPod, cmdReq)} class="flex items-center w-full">
                <input type="text" bind:value={cmdReq} class="bg-black text-white w-full resize-none outline-none no-animation"/>
              </form>
            </div>
          </div>
          {/if}
        </div>
      </div>
    </Collapsible.Content>
  </Collapsible.Root>
</div>

<style>
  .log-container {
    font-family: 'Courier New', Courier, monospace; /* Monospace font */
    white-space: pre-wrap; /* Preserve line breaks */
    overflow-wrap: break-word; /* Allow long words to break */
  }
</style>
